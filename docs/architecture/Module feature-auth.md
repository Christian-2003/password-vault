<img src="../img/icon.png" height="150" align="right">

# Module :feature:auth
The module :feature:auth contains the presentation layer for the authentication within the app Password Vault.

<br/>

### Table of Contents
* [1 Introduction and Goals](#1-introduction-and-goals)
    * [1.1 Functional Requirements](#11-functional-requirements)
    * [1.2 Quality Requirements](#12-quality-requirements)
* [2 Constraints](#2-constraints)
* [3 Context and Scope](#3-context-and-scope)
    * [3.1 Business Context](#31-business-context)
    * [3.2 Technical Context](#32-technical-context)
* [4 Solution Strategy](#4-solution-strategy)
* [5 Building Block View](#5-building-block-view)
    * [5.1 Context and Scope](#51-scope-and-context)
    * [5.2 Level 1](#52-level-1)
    * [5.3 Level 2](#53-level-2)
* [6 Runtime View](#6-runtime-view)
    * [6.1 Setup Flow](#61-setup-flow)
    * [6.2 Login Flow](#62-login-flow)
    * [6.3 Recovery Flow](#63-recovery-flow)
    * [6.4 Change Password Flow](#64-change-password-flow)
    * [6.5 Generate Recovery Codes Flow](#65-generate-recovery-codes-flow)
    * [6.6 Enable Biometrics Flow](#66-enable-biometrics-flow)
* [7 Deployment View](#7-deployment-view)
* [8 Crosscutting Concerns](#8-crosscutting-concerns)
    * [8.1 Domain Concepts](#81-domain-concepts)
    * [8.2 Patterns](#82-patterns)
    * [8.3 Safety and Security](#83-safety-and-security)
        * [8.3.1 Zero Data After Use](#831-zero-data-after-use)
        * [8.3.2 Obfuscate Sensitive Data](#832-obfuscate-sensitive-data)
        * [8.3.3 Prevent Screenshots and Screen Recordings](#833-prevent-screenshots-and-screen-recordings)
* [9 Architectural Decisions](#9-architectural-decisions)
    * [9.1 Package Structure](#91-package-structure)
    * [9.2 Public API](#92-public-api)
    * [9.3 Dependencies](#93-dependencies)
* [10 Quality Requirements](#10-quality-requirements)
    * [10.1 Ease of Use (Q01)](#101-ease-of-use-q01)
    * [10.2 Security (Q02)](#102-security-q02)
* [11 Risks and Technical Debt](#11-risks-and-technical-debt)
    * [11.1 Interactions with Other Modules](#111-interactions-with-other-modules)
    * [11.2 Prevent Screenshots and Screen Recordings](#112-prevent-screenshots-and-screen-recordings)
* [12 Appendix](#12-appendix)
    * [12.1 Glossary](#121-glossary)
    * [12.2 Related Documents](#122-related-documents)

<br/>

## 1 Introduction and Goals
This document describes the authentication feature for the application.

The authentication mechanism within the app is split into two separate modules:
* **:core:security**: This module contains the entire domain and business logic for authentication, which includes unlocking the master key, as well as setting, changing and deleting credentials.
* **:feature:auth (this module)**: This module contains the entire user interface, as well as flow control logic for the authentication. This includes the initial setup flow, as well as flows used to change a master password, regenerate recovery codes, enable and disable biometrics and recover a forgotten master password. Additionally, the module contains the user interface for the initial app login. Authentication before accessing data for the autofill service is _not_ part of this module and handled by the [:feature:autofill](./Module%20feature-autofill.md)-module.

The domain and business logic for the authentication is not part of this module, because the authentication is closely related to the unlocking of the master key, which is a domain concept from :core:security.

The following goals have been established for the feature:

ID | Goal
--- | ---
G01 | The authentication feature shall allow users to authenticate using a master password or device biometrics.
G02 | If the user forgets their master password, recovery codes shall be usable to change the master password.
G03 | Appropriate workflows shall be established in order to facilitate interactions with the authentication feature.

<br/>

### 1.1 Functional Requirements
In order to satisfy the user, the following functional requirements can be established:

ID | Requirement | Description
--- | --- | ---
F01 | Configuration | All authentication-related configuration shall be editable to the user at all times. The master password, recovery codes and biometrics shall be editable.
F02 | Login | The user needs to login to the app (i.e. authenticate) before any app content is accessible.
F03 | Commiting data | Authentication workflows shall be designed in a transaction-based manner. If the user cancels a workflow, data shall not be commited.
F04 | Master Key Handling | App content shall only be displayed after the master key is unlocked successfully. Otherwise, the app would crash trying to access a master key that has not been unlocked.

<br/>

### 1.2 Quality Requirements
The following quality requirements can be established:

ID | Requirement | Description
--- | --- | ---
[Q01](#101-ease-of-use-q01) | Ease of use | Working with authentication shall be straightforward and easy. This includes login, initital setup and changing credentials.
[Q02](#102-security-q02) | Security | Sensitive data needs to be handled securely to prevent unnecessary attack vectors.

<br/>

## 2 Constraints
The following constraints are established:

* Integrated with the Gradle build tool
* Dependencies are managed with version catalog
* Domain and business logic are developed separately within :core:security.

<br/>

## 3 Context and Scope
The following section describes the context and scope for the search feature.

<br/>

### 3.1 Business Context
The following illustration shows the business context for the autofill feature:

![Business Context](./../img/architecture/module%20feature-auth/business_context.drawio.svg)

<br/>

### 3.2 Technical Context
The auth feature is seamlessly integrated into the app by the :app-module. It itself provides the entire user interface for all authentication-related operations (besides authentication for autofill).

The core domain and business logic for the authentication lies entirely within the :core:security-module.

The following diagram illustrates these dependencies:

![Technical Context](./../img/architecture/module%20feature-auth/technical_context.drawio.svg)

<br/>

## 4 Solution Strategy
The feature is implemented as it's own module and only contains the user interface. The domain and business logic is located within :core:security. The reason for this division is as follows:

The authentication is a key part of the process to unlock the master key for subsequent cryptographic operations on sensitive user data. Both the authentication domain model, as well as the processes required for unlocking the master key are closely related and intertwined in a way that makes it unfeasable to separate authentication and master key unlocking.  
Therefore, it is more realistic to keep these two "features" closely related.

The unlocking of the master key is a prerequsitite for any cryptographic operations that might be carried out by the app (for example, when unlocking sensitive data to be used for autofill). Therefore, besides the actual cryptographic implementations, the unlocking logic for the master key needs to be located in the :core:security-module, which is then used by higher-level modules like :data:accounts or [:feature:autofill](./Module%20feature-autofill.md).  
However, the user interface for the authentication is specifically not required by other high-level modules and is only used for authentication-related purposes. Therefore, it is suitable to implement the user interface for the authentication in a separate module.

The following illustration shows how both authentication, as well as cryptographic operations are handled by :core:security:

![Solution Strategy](./../img/architecture/module%20feature-auth/solution_strategy.drawio.svg)

<br/>

## 5 Building Block View
The following figure shows the top-level building blocks of the module, as well as their subdivisions. Since this module conains only the user interface for the authentication logic, the building blocks focus on the description of the user interface workflows.

![Building Blocks](./../img/architecture/module%20feature-auth/building_blocks.drawio.svg)

<br/>

### 5.1 Scope and Context
The following figure defines the scope and context:

![Building Blocks - Scope and Context](./../img/architecture/module%20feature-auth/building_blocks_scopeandcontext.drawio.svg)

The following building blocks can be identified:

Block | Responsibility
--- | ---
User | Primary entity which interacts with the authentication. They enter their master password or provide biometrics for authentication. Additionally, they can recover their master password using recovery codes.
:app | The app module seamlessly integrates the :feature:autofill-module within the app.
:core:security | The module contains the domain and business logic for authentication and master key unlocking.
:feature:auth | Feature implements the user interface for the authentication.

<br/>

### 5.2 Level 1
The following figure defines level 1:

![Building Blocks - Level 1](./../img/architecture/module%20feature-auth/building_blocks_level1.drawio.svg)

The following building blocks can be identified:

Block | Parent | Responsibility
--- | --- | ---
Settings flow | :feature:auth | UI flow that contains the entire settings for the authentication. Settings can be accessed through the in-app settings and allow the user to change their master password, regenerate recovery codes and enable or disable biometrics.
Disable biometrics | :feature:auth | Allows the user to disable biometrics.
Setup flow | :feature:auth | First-time app setup allows the configuration of master password, recovery codes and biometrics. This is only shown for the first time the app is started.
Login flow | :feature:auth | Allows the user to login to the app. This is shown every time the user opens the app. Through this flow, the master password recovery can be started.
Recovery flow | :feature:auth | If the user forgets their master password, the recovery allows the user to provide a new master password after authentication through a recovery code.
Change password flow | :feature:auth | Allows the user to change their master password.
Generate recovery codes flow | :feature:auth | Allows the user to regenerate recovery codes.
Enable biometrics flow | :feature:auth | Allows the user to enable biometrics.

<br/>

### 5.3 Level 2
The following figure defines level 2:

![Building Blocks - Level 2](./../img/architecture/module%20feature-auth/building_blocks_level2.drawio.svg)

The following building blocks can be identified:

Block | Parent | Responsibility
--- | --- | ---
PasswordScreen | Setup flow, <br/>Recovery flow, <br/>Change password flow, <br/>Generate recovery codes flow, <br/>Enable biometrics flow | Allows the user to enter their current master password. This is required to unlock the KEK which is later used by :core:security internally.
RecoveryCodesScreen | Setup flow, <br/>Generate recovery codes flow | Displays newly generated recovery codes to the user.
BiometricsScreen | Setup flow, <br/>Enable biometrics flow | Allows the user to enable biometrics for authentication
FinishScreen | Setup flow, <br/>Recovery flow, <br/>Change password flow, <br/>Generate recovery codes flow, <br/>Enable biometrics flow | Loading screen displayed to the user while saving data. This screen also handles the actual invokations of use cases in :core:security. Only once this screen is reached, the data is saved.
LoginScreen | Login flow | Allows the user to authenticate either through their master password or biometrics. The recovery can be started through this screen.
RecoveryScreen | Recovery flow | Allows the user to enter a recovery code to authenticate before they can set a new master password.

<br/>

## 6 Runtime View
The following section describes the runtime view for the authentication feature. The following UI flows are regarded each:
* [Setup flow](#61-setup-flow)
* [Login flow](#62-login-flow)
* [Recovery flow](#63-recovery-flow)
* [Change password flow](#64-change-password-flow)
* [Generate recovery codes flow](#65-generate-recovery-codes-flow)
* [Enable biometrics flow](#66-enable-biometrics-flow)

<br/>

### 6.1 Setup Flow
The setup flow allows the user to setup the entire authentication in a single flow. The flow is displayed only when no authentication data is present (i.e. when the app is started for the very first time.). Removing authentication data from `SharedPreferences` through a development tool would invoke this flow with the next app start as well.

The following key steps are performed:
1. Enter master password
2. Generate recovery codes and display them to the user
3. If the device supports biometrics, they can be enabled
4. Save data

The following UML sequence diagram illustrates data flow:

![Runtime View Sequence - Setup Flow](./../img/architecture/module%20feature-auth/runtime_view_sequence_setup.drawio.svg)

The following UML diagram illustrates the UI flow:

![Runtime View Activity - Setup Flow](./../img/architecture/module%20feature-auth/runtime_view_activity_setup.drawio.svg)

<br/>

### 6.2 Login Flow
The login flow allows the user to authenticate before accessing the application. From a UI perspective, the LoginScreen is just a gate to the remaining application - there is nothing preventing the system from showing other app screens without going through this flow first. However, the data can only be accessed after the master key is unlocked - which is done through this screen. Therefore, even if other screens were to be displayed before prior authentication, no data can be decrypted.

The following key steps are performed:
1. Enter master password or scan biometrics
2. Try to unlock the master key
3. On success, continue to the app

The following UML sequence diagram illustrates data flow:

![Runtime View Sequence - Login Flow](./../img/architecture/module%20feature-auth/runtime_view_sequence_login.drawio.svg)

The following UML diagram illustrates the UI flow:

![Runtime View Activity - Login Flow](./../img/architecture/module%20feature-auth/runtime_view_activity_login.drawio.svg)

<br/>

### 6.3 Recovery Flow
If the user forgets their master password, the recovery flow can be started from within the [login flow](#62-login-flow).

The following key steps are performed:
1. Enter a recovery code and check whether it is valid
2. Enter a new master password
3. Save new master password
4. On success, go back to login

The following UML sequence diagram illustrates data flow:

![Runtime View Sequence - Recovery Flow](./../img/architecture/module%20feature-auth/runtime_view_sequence_recovery.drawio.svg)

The following UML diagram illustrates the UI flow:

![Runtime View Activity - Recovery Flow](./../img/architecture/module%20feature-auth/runtime_view_activity_recovery.drawio.svg)

<br/>

### 6.4 Change Password Flow
Using the current master password, the user can change their master password.

The following key steps are performed:
1. Enter the current master password and check whether it is valid
2. Enter a new master password
3. Save new master password

The following UML sequence diagram illustrates data flow:

![Runtime View Sequence - Change Flow](./../img/architecture/module%20feature-auth/runtime_view_sequence_change.drawio.svg)

The following UML diagram illustrates the UI flow:

![Runtime View Activity - Change Flow](./../img/architecture/module%20feature-auth/runtime_view_activity_change.drawio.svg)

<br/>

### 6.5 Generate Recovery Codes Flow
In case the user misplaces their recovery codes or determines that they are unsafe otherwise, the app offers the feature to generate new recovery codes. Generally, it is recommended to regenerate the recovery codes regularly.

The following key steps are performed:
1. Enter the current master password and check whether it is valid
2. Generate new recovery codes and offer them for download
3. Save new recovery codes

The following UML sequence diagram illustrates data flow:

![Runtime View Sequence - Generate Codes Flow](./../img/architecture/module%20feature-auth/runtime_view_sequence_generatecodes.drawio.svg)

The following UML diagram illustrates the UI flow:

![Runtime View Activity - Generate Codes Flow](./../img/architecture/module%20feature-auth/runtime_view_activity_generatecodes.drawio.svg)

<br/>

### 6.6 Enable Biometrics Flow
During the [setup flow](#61-setup-flow), the user may decide to not activate biometrics. Alternatively, the setup flow may not show biometrics because the device biometrics are not configured, but the user configures them later. In both cases, the user might want to enable biometrics later. For this purpose, the app offers this as a feature.

The following key steps are performed:
1. Enter the current master password and check whether it is valid
2. Show biometric prompt and authenticate using biometrics
3. Save new authentication method

The following UML sequence diagram illustrates data flow:

![Runtime View Sequence - Biometrics Flow](./../img/architecture/module%20feature-auth/runtime_view_sequence_biometrics.drawio.svg)

The following UML diagram illustrates the UI flow:

![Runtime View Activity - Biometrics Flow](./../img/architecture/module%20feature-auth/runtime_view_activity_biometrics.drawio.svg)

<br/>

## 7 Deployment View
The :feature:autofill-module is deployed within the Password Vault application, which needs to be installed on a device running Android OS.

Inside Password Vault, :feature:autofill is integrated within the existing module architecture.

![Deployment View](./../img/architecture/module%20feature-auth/deployment_view.drawio.svg)

<br/>

## 8 Crosscutting Concerns
This section describes the crosscutting concerns for the module.

<br/>

### 8.1 Domain Concepts
Domain concepts for the authentication are explained in the documentation for the :core:security module.

<br/>

### 8.2 Patterns
Since this module does only contain the user interface for the authentication, the module largely employs patterns regulating the user interface, like:
* MVVM
* SOLID
* DRY

The following diagram illustrates all components of the user interface in regards to their respective MVVM layer:

![Architecture](./../img/architecture/module%20feature-auth/architecture.drawio.svg)

<br/>

### 8.3 Safety and Security
For obvious reasons, security plays a crucial role within this module. Although the security measures to be taken can be greatly reduced since most of the authentication logic is handled by the :core:security module, there are still instances where security measures need to be regarded within this module.

The following security measures are taken into consideration:
1. [Zero data after use](#831-zero-data-after-use)
2. [Obfuscate sensitive data](#832-obfuscate-sensitive-data)
3. [Prevent Screenshots and Screen Recordings](#833-prevent-screenshots-and-screen-recordings)

<br/>

#### 8.3.1 Zero Data After Use
The Android runtime (ART) employs a garbage collector (GC) in order to free unused memory. This makes it easier for developers to manage their data, since they do not have to manage memory themseves. However, this introduces an attack vector through which an attacker can gain access to sensitive data like passwords or recovery codes.

For example, the user needs to enter their master password in order to login to the app. After a successful login, the master password may remain in memory for an unspecified amount of time during which an attacker may read the memory of the Android device, which would provide them access to the master password.

ART does not provide any feature through which to explicitly trigger garbage collection. Although there are exploits that force the garbage collector to run, no method exists that ensures that sensitive data (like a master password) is removed after a specific use case is finished.

Therefore, measures need to be taken that reduce the exposure of sensitive data within memory.

`String` instances must be avoided at all costs. They are immutable and it is impossible to remove or override the data stored within a string. Using a `CharArray` instead allows the manual mutation of it's content. For example, after finishing login, a `CharArray` which stores the master password can be cleared by using `CharArray.fill('\u0000')`. This way, the actual char array might still remain in memory, but it's content is replaced by `'\u0000'`. If an attacker gains access to the memory, they will not see the sensitive data since it has been cleared.

However, there is an important consideration to remind: The controls offered by Jetpack Compose (like `OutlinedTextField`) return their data always as a string. Therefore, the framework still creates `String` instances under the hood outside of our control.  
Regardless, optimizing the workflow we have control over to use char arrays can reduce the occurrences of strings within memory significantly.

The following illustration shows how data is treated within the module:

![Security - Zero Data Flow](./../img/architecture/module%20feature-auth/security_dataflow.drawio.svg)

Now that it is clear how data is transmitted between components within a single UI screen, there are additional challenges. The domain layer commits data only once after a workflow finishes. For example, the setup flow only commits data during the last step. However, the master password is entered in the first step. During intermediate steps, the master password needs to remain in memory (as a char array) until the data is committed. Only after a commit, the sensitive data can be zeroed.

The following UML sequence diagram illustrates this for the setup flow, although the same applies to other UI flows as well:

![Security - Zero Data Sequence](./../img/architecture/module%20feature-auth/security_sequence.drawio.svg)

<br/>

#### 8.3.2 Obfuscate Sensitive Data
Sensitive contents shall be obfuscated in text fields using obfuscation characters such as `'*'`.

<br/>

#### 8.3.3 Prevent Screenshots and Screen Recordings
Using the `FLAG_SECURE`, an Android activity can prevent screen recordings. This flag shall be applied to all screens that contain sensitive contents, like authentication data.

This way, unauthorized and untrusted third-party apps cannot observe the screens.

This feature is currently not implemented.

<br/>

## 9 Architectural Decisions
This section outlines and explains important architectural decisions.

<br/>

### 9.1 Package Structure
The package structure reflects the architectural and design patterns, such as MVVM. The following package structure applies:
```
de.christian2003.feature.auth
+-- model
|   +-- dialogs
|   +-- formatters
|   +-- other
|   +-- states
|
+-- viewmodels
|
+-- ui
|   +-- biometrics
|   +-- finish
|   +-- login
|   +-- password
|   +-- recovery
|   +-- recoverycodes
|   +-- settings
|
+-- di
|
+-- navigation
```

This structure clearly reflects the division into the layers of MVVM (model, view and view model). Furthermore, the packages di and navigation are provided for the following purposes:
* `di`: Setup of the dependency injection using Hilt. Currently, this is unused and empty since there are no classes defined in this module that require DI.
* `navigation`: Setup of the navigation for the module user interface using Jetpack Navigation.

<br/>

### 9.2 Public API
The public API contains the components that are intended to be used by other modules:

Public API | Package | Description
--- | --- | ---
`fun NavGraphBuilder.setupFlow(...)` | `navigation` | Extension method for `NavGraphBuilder` is used to insert the setup flow into the Jetpack Compose navigation of the :app-module.
`object SetupFlow` | `navigation` | Route object for the navigation setup flow destination.
`fun NavGraphBuilder.authSettingsFlow(...)` | `navigation` | Extension method for `NavGraphBuilder` is used to insert the auth settings flow into the Jetpack Compose navigation of the :app-module.
`object AuthSettingsFlow` | `navigation` | Route object for the navigation auth settings flow destination.
`fun NavGraphBuilder.recoveryFlow(...)` | `navigation` | Extension method for `NavGraphBuilder` is used to insert the recovery flow into the Jetpack Compose navigation of the :app-module.
`object RecoveryFlow` | `navigation` | Route object for the navigation recovery flow destination.
`fun NavGraphBuilder.loginDestination(...)` | `navigation` | Extension method for `NavGraphBuilder` is used to insert the login screen into the Jetpack Compose navigation of the :app-module.
`object LoginDestination` | `navigation` | Route object for the navigation login destination.

<br/>

### 9.3 Dependencies
The following dependencies are required by the module:

Dependency | Usage
--- | ---
`:core:common` | Required for formatting of dates and times.
`:core:ui` | Required for common UI.
`:core:security` | Required for authentication domain logic.

<br/>

## 10 Quality Requirements
This section contains all quality requirements that need to be addressed by the feature.

<br/>

### 10.1 Ease of Use (Q01)
All workflows are to be designed in a way that the user can easily navigate them. Navigation errors shall be prevented.

For example, if a user needs to enter the master password before they can navigate to the next screen of a UI flow, the button to continue shall be disabled until the password is entered.

Furthermore, if the data entered is required down the line for subsequent steps, the screen should verify it's validity and only continue if the data is valid.  
For example, the flow to regenerate the recovery codes shall only continue past the password screen if the password entered is valid.

<br/>

### 10.2 Security (Q02)
Sensitive data needs to be handled securely at all cost. This has the highest priority in order to prevent any accidental attack vectors.

Data security for the user interface is specifically described more closely in chapter [8.3 Safety and Security](#83-safety-and-security).

Furthermore, additional information about business and domain security can be found in the documentation of the :core:security module.

Additionally, the [Authentication Risk Analysis](./../security/Authentication%20Risk%20Analysis.md) analyzes possible attack vectors to the authentication as well as mitigation strategies.

<br/>

## 11 Risks and Technical Debt
This section outlines the risks and technical debt associated with the module.

<br/>

### 11.1 Interactions with Other Modules
The domain and business logic for the authentication is located in :core:security. This logic is accessed through the use cases of the public API of :core:security.

Ideally, calling these use cases would be abstracted through an infrastructure-interface within this module as follows:
```
+------------------------+        +---------+    +-----------+    +----------+
| UnlockMasterKeyUseCase |   <-   | AuthApi | <- | ViewModel | <- | Activity |
+------------------------+        +---------+    +-----------+    +----------+

:core:security                    :feature:auth
```

However, this module calls these foreign use cases immediately without any abstraction. Changes in the use cases of other modules require changes to this module as well. However, assuming that we have full control over the app, we can keep changes to use cases to a minimum.

<br/>

### 11.2 Prevent Screenshots and Screen Recordings
The prevention of screenshots and recordings through third-party apps or the Android OS is currently not implemented and needs to be added in the future.

<br/>

## 12 Appendix
The following section contains the appendix for this document.

<br/>

### 12.1 Glossary
The following terms and abbreviations are used in this document:

Term | Description
--- | ---
ART | Android Runtime
DI | Dependency Injection
DRY | Dont repeat yourself
GC | Garbage Collector
IME | Input Method Editor (e.g. a system keyboard)
MVVM | Model View ViewModel

<br/>

### 12.2 Related Documents
The following documents are related:

Document | Description
--- | ---
[Module feature-autofill.md](./Module%20feature-autofill.md) | Module architecture for the autofill feature. The module handles authentication itself for the autofill service.
[Authentication Risk Analysis.md](./../security/Authentication%20Risk%20Analysis.md) | Analysis for risks associated with the authentication feature. This document focuses on attack vectors and appropriate mitigations.

<br/>

***
2026-03-13  
&copy; Christian-2003
