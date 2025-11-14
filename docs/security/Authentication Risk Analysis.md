<img src="../img/icon.png" height="150" align="right">

# Authenticaction Risk Analysis
This document analyzes possible risks and attack vectors to the authentication mechanisms of the Password Vault application for Android.

###### Table of Contents
1. [Scope](#scope)
2. [System Overview](#system-overview)
3. [Attack Vectors and Risk Analysis](#attack-vectors-and-risk-analysis)

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

The flow shows that the app prompts the user for authentication before they can access app data. They can choose between three options:
* **Password Authentication:** The user authenticates through their master password.
* **Biometric Authentication:** If configured, the user authenticates through biometrics, such as fingerprints or facial recognition.
* **Recovery:** If configured, the user can recover their master password through security questions.

###### Data Storage
Throughout the app lifecycle, data related to authentication is stored in multiple spaces. These include the following:
* Android `SharedPreferences`
* Android `KeyStore`
* Temporary storage areas such as RAM, CPU caches or registers

Data is not permitted to be stored in permanent storage areas such as an SSD.

Data flows as follows:
* **Password Setup:** Entry -> Hashing -> Storage in `SharedPreferences`
* **Recovery Setup:** Entry -> Hashing -> Storage in `SharedPreferences`
* **Password Authentication:** Entry -> Hashing -> Compare with password hash in `SharedPreferences`
* **Recovery Authentication:** Entry -> Hashing -> Compare with password hash in `SharedPreferences`

<br/>

## Attack Vectors and Risk Analysis
This section describes a list of possible attacks to the authentication implementation for the app.

###### Overview
This table summarizes attack vectors and their mitigations. 

Attack Vector | Preconditions | Likelihood | Impact
--- | --- | --- | ---
Offline brute-force / dictionary attack | Access to the device storage | Very likely | Very High
Physical device compromise | Access to the (unlocked) physical device | Unlikely | Low, unless device is acquired by professionals (e.g. law enforcement)

Each attack vector is described in greater detail in further sections.

###### Offline Brute-Force / Dictionary Attack
**How:**  
An attacker obtains the stored `password_hash` and `password_salt` (or salts and hashes for the answers to security questions). They run password guesses on their own machine, deriving candidate keys with PBKDF2 and comparing hashes.

**Why practical:**  
Everything needed (salt + verifier) is present and the comparison is local and unlimited. The attacker can use GPUs / CPUs to try many guesses. 65535 PBKDF2 iterations with SHA-512 is decent but may be feasable to brute-force weak passwords (e.g. low entropy or commpon phrases).  
If security questions are low entropy (e.g. names, birthdays or common answers), the threshold of 4 questions still enables successful account recovery by guessing common answers.

**Mitigation:**   
Increase the iterations to 600,000. This is above the [OWASP recommendation](https://en.wikipedia.org/wiki/PBKDF2#Purpose_and_operation) of 210,000 iterations for PBKDF2 with SHA-512. This significantly increases the time required to hash a password by almost 4 times.  
Introduce a common pepper that is added to the process. This pepper is stored within the Android `KeyStore` and cannot be extracted from the Android device. Therefore, any attacks must happen on the device itself and cannot rely on powerful hardware that is unavailable on Android.

###### Physical Device Compromise
**How:**  
An attacker gets phyiscal access and either (a) boots into recovery; (b) obtains a full filesystem image via forensic tool; (c) uses `adb` to pull `SharedPreferences` if debug features or backups are enabled; (d) extracts internal storage on a rooted device.

**Why practical:**  
`Context.PRIVATE` only prevents normal apps from reading files on unrooted devices. It does not protect against root or physical device acquisition. If the device is unlocked by the user or not encrypted, the process is even easier for the attacker.

**Mitigation:**  
Threats to a physical device acquisition cannot be mitigated entirely. Even the introduction of hardware-backed keys, such as through `KeyStore`, cannot prevent the derivation of the user password, since forensic capabilities of an attacker (e.g. law enforement) can access TPM implementations on a physical device. However, hardware-backed keys significantly increase the cost of retrieving such information and prevent any data breach through unprofessionals, such as script-kiddies.  
Protection against rooted devices is out of scope for this analysis.

###### Backups and Synchronization Leaks
...

###### Malicious App or Malware with Priviliges
...

###### In-memory Exposure and Logging
...

###### Side-Channel / Timing / Comparator Attacks
...

###### Threats to Security Questions
...

###### UI and OS Features
...

