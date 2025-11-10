<img src="../img/icon.png" height="150" align="right">

# Authenticaction Risk Analysis
This document analyzes possible risks and attack vectors to the authentication mechanisms of the Password Vault application for Android.

###### Table of Contents
1. [Scope](#scope)
2. [System Overview](#system-overview)

<br/>

## Scope
This analysis applies to the Password Vault application for Android, and considers the authentication mechanisms that are implemented beginning with version 3.8.0 and later.

###### What is In-Scope
The following aspects are considered "in scope" and are regarded by this analysis:
* Local-only auhentication
* `SharedPreferences` storage
* `KeyStore` storage
* Optional biometric authentication
* Optional security questions

###### What is Out-Scope
The following aspects are considered "out of scope" and are therefore not regarded any further:
* Server-side attacks
* Network transit risks
* User-related issues such as social engineering
* Rooted Android devices

###### Infrastructure Assumptions
The following assumptions are made, in order for this analysis to apply:
* Password Vault version 3.8.0 or higher is used
* Android OS version 14 (API 34) or higher is installed
* No remote backend server is present

###### Threat Model Assumptions
The following assumptions apply to possible threats that the app needs to face:
* Attackers have full access to the device storage
* Attackers have access to an unlocked Android device without OS-level data protection
* Attackers have installed malware on the Android device

<br/>

## System Overview
This section provides a brief overview of the system that is implemented to handle user authentication.

###### Architecture
The following diagram briefly illustrates the overall architecture for the authentication system:
![Architecture](../img/security/auth_architecture_overview.drawio.svg) 

The diagram shows the interaction between the user and the interface.  
The interface itself works with application layer use cases that encapsulate all business logic, such as password validation and security question verification.  
The use cases access the specific data on the disk through a repository, which itself accesses external Android dependencies, such as `SharedPreferences` or `KeyStore`.

###### UI Flow
The UI flow is illustrated through the following diagram:
![Architecture](../img/security/ui_flow.drawio.svg) 


