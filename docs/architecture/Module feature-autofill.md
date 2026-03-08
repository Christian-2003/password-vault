<img src="../img/icon.png" height="150" align="right">

# Module :feature:autofill
The :feature:autofill-module contains the entire functionality for the autofill service that is used to fill account data in other apps.

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
    * [6.1 Service Invokation](#61-service-invokation)
    * [6.2 Authentication Invokation](#62-authentication-invokation)
* [7 Deployment View](#7-deployment-view)
* [8 Crosscutting Concerns](#8-crosscutting-concerns)
    * [8.1 Domain Concepts](#81-domain-concepts)
    * [8.2 Patterns](#82-patterns)
* [9 Architectural Decisions](#9-architectural-decisions)
    * [9.1 Package Structure](#91-package-structure)
    * [9.2 Public API](#92-public-api)
    * [9.3 Dependencies](#93-dependencies)
* [10 Quality Requirements](#10-quality-requirements)
    * [10.1 Ease of Use (Q01)](#101-ease-of-use-q01)
    * [10.2 Performance (Q02)](#102-performance-q02)
    * [10.3 Security (Q03)](#103-security-q03)
* [11 Risks and Technical Debt](#11-risks-and-technical-debt)
    * [11.1 Interactions with Other Modules](#111-interactions-with-other-modules)
    * [11.2 Concurrency](#112-concurrency)
* [12 Glossary](#12-glossary)

<br/>

## 1 Introduction and Goals
This document describes the autofill feature, which allows users to automatically fill account data in other apps installed on their Android device.

The configuration of the autofill feature is considered domain logic for the entire app accounts feature. Therefore, this configuration is not implemented in this module, but rather the :data:accounts and :feature:accounts modules. The :feature:autofill module only considers the actual autofill business logic that is invoked by the Android OS autofill subsystem.

The following goals have been established for the feature:

ID | Goal
--- | ---
G01 | The autofill feature shall provide data only after prior authentication.
G02 | The autofill feature shall provide data only to trusted third-party apps, services and websites.
G03 | Most autofill hints provided by the Android OS shall be supported for autofill operations, so that most data can be auto-filled into suitable fields.
G04 | Data shall be partitioned, so that leaking sensitive data into hidden fields is minimized.
G05 | If possible, data should be parsed into individual parts so that they can be auto-filled into separate fields. Example: An address like "Platz der Republik 1, 11011 Berlin" should be parsed into ["Platz der Republik", "11011", "Berlin"]. This way, autofillable fields for Street name, postal code and city can be auto-filled, even though the data was not specified for each field individually.
G06 | Users shall be able to enable and disable the feature from within the app.
G07 | If users decide to no longer use autofill, the app shall block all requests.
G08 | Generally, autofill requests from Password Vault itself shall be denied.
G09 | If multiple accounts are configured for an auto-fillable app, all accounts shall be provided for autofill-operations.

<br/>

### 1.1 Functional Requirements
In order to satisfy the user, the following functional requirements can be established:

ID | Requirement | Description
--- | --- | ---
F01 | Intuitive configuration | The autofill data (i.e. account details) shall be configurable comfortably - ideally, with no additional input from the user.
F02 | Find accounts | Only suitable accounts shall be auto-filled into remote apps. For example, if the user auto-fills credentials for the app "Netflix", the system should not provide credentials for "GitHub".
F03 | Secure authentication | The user shall be authenticated before any data is decrypted and auto-filled.
F04 | Data partitioning | Data shall not be leaked to invisible input fields. For this, data partitioning is required.
F05 | Configuration | The user shall be able to configure whether they want to use the autofill service. Additionally, if uncontrolled remote APIs (e.g. Geocoder) are required, users shall be able to turn off these specific features for privacy.
F06 | Protection and privacy | Data shall be protected at all times and only be released to other apps and services for autofill. Privacy has the upmost priority.

<br/>

### 1.2 Quality Requirements
The following quality requirements can be established:

ID | Requirement | Description
--- | --- | ---
[Q01](#101-ease-of-use-q01) | Ease of use | All information required to autofill data into other apps shall be configurable easily without much effort.
[Q02](#102-performance-q02) | Performance | The time consumption for autofill-requests shall be as small as possible, since a lot of requests can be performed sequentially. Since requests run in the background, resource usage shall be reduced as much as possible.
[Q03](#103-security-q03) | Security | Data shall not be auto-fillable to untrusted apps, services or websites.

<br/>

## 2 Constraints
The following constraints are established:

* Integrated with the Gradle build tool
* Dependencies are managed with version catalog

<br/>

## 3 Context and Scope
The following section describes the context and scope for the autofill feature.

<br/>

### 3.1 Business Context
The following illustration shows the business context for the autofill feature:

![Business Context](./../img/architecture/module%20feature-autofill/business_context.drawio.svg)

<br/>

### 3.2 Technical Context
The autofill feature is accessed by the autofill system of Android. Requests and responses are always routed through the operating system. This way, the auto-fillable app does not know anything about the autofill service provider (i.e. Password Vault) and vice versa.

The module depends on all core modules in order to access common busines logic, ui elements as well as the authentication and master key unlocking system provided by :core:security.

Additionally the module depends on the :data:accounts-module. The :data:accounts-module is required to get access to accounts for autofill requests.

The following diagram illustrates these dependencies:

![Technical Context](./../img/architecture/module%20feature-autofill/technical_context.drawio.svg)

<br/>

## 4 Solution Strategy
The autofill feature is implemented as a separate module that relies on :account:data to get access to account data and autofillable details.

The autofill support is implemented in a custom module in order to satisfy separation of concerns. The business logic and domain model within this module are almost completely related to the autofill logic. Inserting this into a different module like :feature:accounts could create confusion and would increase the size and complexity of the module. Therefore, the creation of a separate module is preferred.

Furthermore, the realization as a separate module allows to hide the internals of the business logic and domain concepts, so that they are invisible to the rest of the app. This is preferred, since the entire logic and model are relevant to the autofill system only.

Internally, the module handles the following:
* Implementation of the domain layer for the autofill logic.
* Implementation of business logic for autofill requests. This includes selecting data as well as parsing them into a suitable format for autofillable input fields.
* Interaction with the Android autofill system to be invoked for fill requests and the provision of fill responses after requests were performed successfully.
* Presentation of configuration as well as authentication screens.

<br/>

## 5 Building Block View
The following figure shows the top-level building blocks of the module, as well as their subdivisions:

![Building Blocks](./../img/architecture/module%20feature-autofill/building_blocks.drawio.svg)

<br/>

### 5.1 Scope and Context
The following figure defines the scope and context:

![Building Blocks - Scope and Context](./../img/architecture/module%20feature-autofill/building_blocks_scopeandcontext.drawio.svg)

The following building blocks can be identified:

Block | Responsibility
--- | ---
User | The user is one of two entities that interact with the app for the autofill service. They interact with the user interface of the app directly, for example for authentication. Furthermore, they interact with other apps where they can select input fields which trigger the autofill system of Android.
Other app | For any app installed on the device, the autofill system can be invoked once the user selects an auto-fillable input field.
Autofill system | The Android autofill system is invoked once an input-field is selected within any app. It itself invokes the autofill service provided by Password Vault.
:data:accounts | The module is responsible for modelling, storing and loading accounts. The :feature:autofill-module uses this module to access accounts and account details.
:core:security | Handles authentication before data can be decrypted and accessed.
:app | The app module seamlessly integrates the :feature:autofill-module within the app.
:feature:autofill | Implementation for the autofill system provided by Password Vault.

<br/>

### 5.2 Level 1
The following figure defines level 1:

![Building Blocks - Level 1](./../img/architecture/module%20feature-autofill/building_blocks_level1.drawio.svg)

The following building blocks can be identified:

Block | Parent | Responsibility
--- | --- | ---
Autofill service | :feature:autofill | Implements all logic for the autofill service that can be invoked by the Android system in order to fill remote views. All authentication logic is not part of this module.
User interface | :feature:autofill | Implements the user interface with which the user interacts. This includes both screens for authentication, as well as user configuration.
User config | :feature:autofill | Implements the user configuration for the module. 

<br/>

### 5.3 Level 2
The following figure defines level 2:

![Building Blocks - Level 2](./../img/architecture/module%20feature-autofill/building_blocks_level2.drawio.svg)

The following building blocks can be identified:

Block | Parent | Responsibility
--- | --- | ---
PasswordVaultAutofillService | Autofill service | Implements the AutofillService that is invoked by the Android autofill system for fill reqests. It checks whether data can be auto-filled. If data can be auto-filled, the service starts the authentication process.
AssistStructureFetcher | Autofill service | From the fill contexts provided to the autofill service, the fetcher retrieves the assist structure that should be auto-filled.
AssistStructureParser | Autofill service | Parses an assist structure and returns the autofill types that are present within it.
Target validation | Autofill service | Validates whether the app that is requesting a fill-operation is trused and valid.
FetchAutofillDataUseCase | Autofill service | Use case that returns the auto-fillable data for the input fields of the app that requested autofill.
AutofillAuthActivity | User interface | Activity that handles the authentication before data can be decrypted, accessed and auto-filled.
AutofillAuthViewModel | User interface | View model for the activity that handles the authentication.
AutofillSettingsScreen | User interface | Screen for the autofill settings displayed within the app.
AutofillSettingsViewModel | User interface | View model for the screen for the autofill settings
Repository | User config | User config | Abstraction for the user configuration repository.
Concrete Repository | User config | Concrete implementation for the repository handling the user config. This uses the `SharedPreferences` and Android `AutofillManager` internally.

<br/>

## 6 Runtime View
The architecture documentation focuses on the main scenario of this module, which is the orchestration of a autofill requests. The module has other components as well, such as the user config. These other modules are disregarded in this section for brevity and in order to focus on the main feature.

The key steps for an autofill request are:
1. User focuses an input field in another app
2. The Android autofill system invokes the autofill service from Password Vault
3. The autofill service checks whether data can be auto-filled and whether the other app is valid
4. The user authenticates with Password Vault
5. Password Vault queries auto-fillable data and returns the data to the Android autofill system
6. The other system fills the provided data into input fields

The following UML sequence diagram illustrates the general sequences for the autofill system if every check is successful (i.e. data is availalbe, the other app is valid and the authentication is successful):

![Runtime View Sequence](./../img/architecture/module%20feature-autofill/runtime_view_sequence.drawio.svg)

<br/>

### 6.1 Service Invokation
The autofill service is invoked by the autofill system through the `onFillRequest`-method.

The following key steps are orchestrated by the autofill service of Password Vault:
1. From the provided fill contexts, the active assist structure is fetched. The active assist structure is denominated by the input field that is focused by the user.
2. The active assit structure is parsed, resulting in autofill types that are mapped to `AutofillId`. The `AutofillId` describes the input field from which the type was retrieved. This is required later to define in which input field data shall be filled.
3. Accounts are queried that are suitable for autofill-operations. Critera for filtering are: (1) They have a target that matches the other app and (2) they contain details whose type can be used to fill the autofill types parsed in step 2.
4. Check whether the other app is valid. The other app's signing certificate's fingerprint must match the fingerprint that is stored within Password Vault (The stored fingerprint is generated during setup once the user selects the app as target).
5. Generate the FillResponse that is used for authentication, which is shown to the user within the other app or system IME.
6. Return the fill response.

The following sequence diagram illustrates the steps for the service invokation:

![Runtime View Sequence - Service](./../img/architecture/module%20feature-autofill/runtime_view_sequence_service.drawio.svg)

Once the autofill services finishes and returns the fill response, the Android system shows the authentication presentation to the user which indicates that they can autofill. Once the user clicks on the autofill presentation, the [authentication](#62-authentication-invokation) gets invoked.

<br/>

### 6.2 Authentication Invokation
After the autofill service finishes and the user clicks on the authentication presentation in the UI of the Android autofill system, the Password Vault authentication get's invoked through which the user needs to authenticate before any data can be decrypted and accessed.

The following key steps are performed:
1. The authentication UI is shown to the user. They can choose to enter the master password (or authenticate through biometrics - this is omitted in the sequence diagram below).
2. Once the password is entered and the user chooses to authenticate, a request is sent to the :core:security-module to unlock the master key. If successful, the authentication UI is hidden from the user.
3. All accounts that can be auto-filled into the other app are queried from the :data:accounts-module.
4. The details are mapped to autofill types based on their detail types.
5. If required, the content of details can be parsed into a different format so that it can be filled in specific autofillable input fields. The following parsers are available:
    * **AddressParser:** Parses an address like "Platz der Republik 1 , 11011 Berlin, Deutschland" into ["Platz der Republik 1", "11011", "Berlin", "Deutschland"].
    * **DateParser:** Parses a date like "11.09.2001" into ["11", "09", "2001"].
    * **PersonNameParser:** Parses a personal name like "Max Peter Mustermann" into ["Max", "Peter", "P", "Mustermann"].
    * **PhoneNumberParser:** Parses a phone number like "+49 1234 567890" into ["+49", "1234", "567890"].
6. Presentation is built for every dataset.
7. The presentation is returned to the autofill system.

The following sequence diagram illustrates the steps for the authentication invokation:

![Runtime View Sequence - Authentication](./../img/architecture/module%20feature-autofill/runtime_view_sequence_auth.drawio.svg)

<br/>

## 7 Deployment View
The :feature:autofill-module is deployed within the Password Vault application, which needs to be installed on a device running Android OS.

Inside Password Vault, :feature:autofill is integrated within the existing module architecture.

The module provides in implementation for the `AutofillService`-class that can get invoked by the `AutofillManager` of the Android autofill system, once an input field is focused in another app.

![Deployment View](./../img/architecture/module%20feature-autofill/deployment_view.drawio.svg)

<br/>

## 8 Crosscutting Concerns
This section describes the crosscutting concerns for the module.

<br/>

### 8.1 Domain Concepts
The domain model for this module is rather simple and focuses entirely on modelling autofill data.

![Domain Concepts](./../img/architecture/module%20feature-autofill/domain_concepts.drawio.svg)

The main class from this model is `AutofillResponse`, which stores the response data that can be auto-filled for a single account. If multiple accounts are available for autofill, multiple instances of `AutofillResponse` are generated - one for each account.

The autofill response contains a list of autofill items. An `AutofillItem` instance models the autofillable data for an autofill type. It contains a label (e.g "Password") and a content (e.g. "MySecretPassword123").

The autofill type is described by an enum, where each entry corresponds to an autofill hint provided by the Android OS. Each typ references a partition and group.

A partition describes the data that can be returned together with a single autofill response. For example, assume a screen has three input fields: "Username", "Password" and "Credit Card Number". If the user selects the input field "Username", Password Vault only returns data for "Username" and "Password", because they are part of the same partition. The "Credit Card Number" is in a different partition and is therefore not returned. To autofill the credit card number, the user needs to select the field and go through the entire autofill workflow again. This limits leaking sensitive data accidentally (for example into hidden fields that are provided my malicious apps).

`AutofillGroup` describes fields that belong together logically. For example, input fields for "PostalCode", "PostalAddress", "AddressCountry", belong together. The data for these fields is likely stored within a single detail inside an account. This way, the app knows whether multiple fields belong together.

The following graphic illustrates the concept of autofill groups:

![Domain Concepts - Autofill Groups](./../img/architecture/module%20feature-autofill/domain_concepts_autofill_groups.drawio.svg)

<br/>

### 8.2 Patterns
The implementation shall utilize common programming, design and architecture patterns.

The following patterns are utilized:
* MVVM
* SOLID
* DRY
* Clean Architecture

The following diagram illustrates the implementation of clean architecture:

![Architecture](./../img/architecture/module%20feature-autofill/architecture.drawio.svg)

<br/>

## 9 Architectural Decisions
This section outlines and explains important architectural decisions.

<br/>

### 9.1 Package Structure
The package structure reflects the architectural and design patterns, such as clean architecture and MVVM. The following package structure applies:
```
de.christian2003.feature.autofill
+-- domain
|   +-- entities
|   +-- repositories
|   +-- services
|
+-- application
|   +-- services
|   +-- usecases
|
+-- infrastructure
|   +-- android
|   +-- factories
|   +-- repositories
|   +-- services
|
+-- presentation
|   +-- models
|   +-- ui
|   |   +-- auth
|   |   +-- settings
|   +-- viewmodels
|
+-- di
|
+-- navigation
```

This structure clearly reflects the division into the layers of clean architecture (domain, application and infrastructure / presentation). Furthermore, the packages di and navigation are provided for the following purposes:
* `di`: Setup of the dependency injection using Hilt.
* `navigation`: Setup of the navigation for the module user interface using Jetpack Navigation.

<br/>

### 9.2 Public API
The public API contains the components that are intended to be used by other modules or the Android system:

Public API | Package | Description
--- | --- | ---
`fun NavGraphBuilder.autofillSettingsFlow(...)` | `navigation` | Extension method for `NavGraphBuilder` is used to insert the autofill settings flow into the Jetpack Compose navigation from the :app-module.
`object AutofillSettingsFlow` | `navigation` | Route object for the navigation flow destination mentioned above.
`class PasswordVaultAutofillService` | `infrastructure.android` | Implementation of the autofill service invoked by the Android autofill system. This class is not intended for use within the app. Rather, the class shall only be invoked by the Android OS.

<br/>

### 9.3 Dependencies
The following dependencies are required by the module:

Dependency | Usage
--- | ---
`:core:common` | Required for formatting of dates and times.
`:core:ui` | Required for common UI.
`:core:security` | Required for authentication and unlocking the master key.
`:data:accounts` | Required to fetch accounts for autofill operations.

<br/>

## 10 Quality Requirements
This section contains all quality requirements that need to be addressed by the feature.

<br/>

### 10.1 Ease of Use (Q01)
The autofill feature needs to know what kind of data can be auto-filled. Since the actual data can only be decrypted and accessed after authentication, some additional metadata is required to identify which type of data is available.

For this reason, an additional field called "Type" is added which needs to be configured by the user. This type describes the type of a detail for the autofill system. This type is very similar but not identical to the type from Password Vault v3.7.4 and lower.

The following types are available for configuration:

Type | Example | Description
--- | --- | ---
Email | max.mustermann@gmail.com | Email addresses
Username | xX_Max1234_Xx | Usernames provided for an app or website
Password | MySecretPassword123 | Passwords
Pin | 1234 | Pin codes (similar to passwords but numerical). This is requird to provide the user an additional level of separation between passwords and pins - although they are identical from the perspectice of the autofill service.
Security question | What's your favorite animal? Dog. | Security questions. They are unused by autofill, since there is no default autofill hint for security questions.
Personal name | Max Mustermann | Personal name.
Phone number | +49 1234 567890 | Phone number.
Date | 11.09.2001 | Dates, for example birthdays. Once a date is encountered, it is always treated as a birthday by the autofill service, regardless of whether it is an actual birthday or some other date the user wants to remember for an account.
Url | https://www.youtube.com | URL. Unused by autofill, since there is no default autofill hint for Android.
Address | Platz der Republik, 11011 Berlin, Deutschland | Address.
Credit card number | DE01 2345 6789 0123 4567 89 | Credit card or international banking numbers.
Text | Hello, World | Can be used if the information cannot be classified by any other type. Details of this type are disregarded by autofill.
Number | 1234 | Similar to text-type, but for numbers.

Types are part of the domain model for accounts. Therefore they are not part of this module. However, due to their importance, they shall be named here specifically.

<br/>

### 10.2 Performance (Q02)
Performance is a crucial aspect for autofill request. There are two main aspects to this quality requirement:
* Time consumption
* Resource usage

Even though autofill requests run asynchronously and do not block the other app's execution, tiem consumption shall be reduced to a minimum. Users shall not experience a noticable delay for request as this results in bad user experience.

Resource usage needs to be reduced to a minimum as well. The autofill service runs asynchronoulsy in the background. If it consumes too much memory, the Android OS can decide to kill the process which prevents any fill request from finishing to completion. Therefore, if too much memory is used, autofill requests may not terminate successfully.

Reducing time consumption can be achieved through clever use of concurrency, although this might significantly increase the parallel memory consumption. Therefore, concurrency is not implemented so far. This has to be analyzed furhter.

<br/>

### 10.3 Security (Q03)
Data may only be released to the autofill system (and the app that shall be auto-filled), once the user authenticates. Since Password Vault implements "Security by Design", it is impossible to access data without prior authentication.

Data can only be decrypted from a key that is inaccessible without the master password or biometrics. Therefore, this requirement is fulfilled at all times.

<br/>

## 11 Risks and Technical Debt
This section outlines the risks and technical debt associated with the module.

<br/>

### 11.1 Interactions with Other Modules
Functionality of other modules are usually provided through their own use case classes, such as `GetAllAccountsUseCase` or `GetAccountByIdUseCase`. These use cases can be subject to change.

Ideally, calling these use cases would be abstracted through an infrastructure-interface within this module as follows:
```
+-----------------------+        +-------------+    +--------------------------+    +-----------+    +----------+
| GetAllAccountsUseCase |   <-   | AccountsApi | <- | FetchAutofillDataUseCase | <- | ViewModel | <- | Activity |
+-----------------------+        +-------------+    +--------------------------+    +-----------+    +----------+

:data:accounts                   :feature:autofill
```

However, this module calls these foreign use cases immediately without any abstraction. Changes in the use cases of other modules require changes to this module as well. However, assuming that we have full control over the app, we can keep changes to use cases to a minimum.

<br/>

### 11.2 Concurrency
Due to large numbers of data being accessed, concurrency should be utilized to improve performance. This has been named as a requirement specifically ([10.3 Performance (Q03)](#103-performance-q03)).

However, no concurrency is employed at the moment to improve performance since this may increase memory consumption significantly which may lead to the termination of the service process by Android. This needs to be analyzed furhter.

<br/>

## 12 Glossary
The following terms and abbreviations are used in this document:

Term | Description
--- | ---
DI | Dependency Injection
DRY | Dont repeat yourself
IME | Input Method Editor (e.g. a system keyboard)
MVVM | Model View ViewModel
SOLID | Single Responsibility, Open-Closed, Liskov Substitution, Interface Segregation and Dependency Inversion

<br/>

***
2026-03-08  
&copy; Christian-2003
