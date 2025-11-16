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
[Offline brute-force / dictionary attack](#offline-brute-force--dictionary-attack) | Access to the device storage | Very likely | Very High
[Physical device compromise](#physical-device-compromise) | Access to the (unlocked) physical device | Unlikely | Low, unless device is acquired by professionals (e.g. law enforcement)
[Backups and synchronization leaks](#backups-and-synchronization-leaks) | App files are backed-up | Unlikely | Very High
[Malicious app or malware with priviliges](#malicious-app-or-malware-with-priviliges) | Malware, third-party IME or Accessibility Services capture inputs (like passwords) before they are processed | Likely | Very High
[In-memory exposure and logging](#in-memory-exposure-and-logging) | Sensitve data is stored in temporary memory or is included in logs | Very likely | High
[Side-channel / timing / comparator Attacks](#side-channel--timing--comparator-attacks) | Attacker can observe timing differences when verifying passwords | Likely | Low
[Threats to security questions](#threats-to-security-questions) | Users uses low-entropy answers | Very Likely | Very High
[UI and OS features](#ui-and-os-features) | UI or OS leaks sensitive app content, such as through autofill or screenshots | Very Likely | Medium

Each attack vector is described in greater detail in further sections.

###### Offline Brute-Force / Dictionary Attack
**How:**  
An attacker obtains the stored `password_hash` and `password_salt` (or salts and hashes for the answers to security questions). They run password guesses on their own machine, deriving candidate keys with PBKDF2 and comparing hashes.

**Why practical:**  
Everything needed (salt + verifier) is present and the comparison is local and unlimited. The attacker can use GPUs / CPUs to try many guesses. 65535 PBKDF2 iterations with SHA-512 is decent but may be feasable to brute-force weak passwords (e.g. low entropy or commpon phrases).  
If security questions are low entropy (e.g. names, birthdays or common answers), the threshold of 4 questions still enables successful account recovery by guessing common answers.

**Mitigation:**   
Increase the iterations to 600,000. This is above the [OWASP recommendation](https://en.wikipedia.org/wiki/PBKDF2#Purpose_and_operation) of 210,000 iterations for PBKDF2 with SHA-512. This significantly increases the time required to hash a password by almost 10 times.  
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
**How:**  
This attack occurs when `SharedPreferences` or other internal app files are included in device backups (e.g. Android Auto Backup, Google Cloud Backup or `adb` backups). If backups are stored on a PC, cloud service or synched to another device, an attacker who compromises those locations can extract the stored password hash, salts and hashed security answers. Even without compromising the device directly, the attacker can obtain the authentication data indirectly via these backup channels.

**Why practical:**  
By default, many Android apps unintentionally allow backups through `android:allowBackup=true` in the manifest. End users often synchronize their device backups without realizing what is included, and third-party backup tools may capture internal app storage. Since backups are frequently stored in less secure environments (e.g. a PC without disk encryption, clound accounts protected by weak passwords), an attacker may find it easier to compromise the backup storage than the device itself. Once the `SharedPreferences` file is obtained, [offline brute-force](#offline-brute-force--dictionary-attack) attempts become possible with no rate-limiting.

**Mitigation:**  
Disable app backups by setting `android:allowBackup=false` in the manifest and explicitly excluding `SharedPreferences` from all backup mechanisms. Educate users about third-party backup risks. Consider encrypting sensitive data with a hardware-backed key from a KeyStore, so even if the backed-up files are extracted, the data cannot be used without the device.

###### Malicious App or Malware with Priviliges
**How:**  
A malicious app installed on the device can exploit powerful Android permissions - most notably Accessibility Services or a custom IME - to intercept user input. Such malware can monitor screen content, read typed passwords or security answers in real time, or even capture on-screen UI elements. More advanced malware can escalate priviliges if the device is rooted or compromised.

**Why practical:**  
Accessibility services are routinely abused because many users grant them to apps without understanding their full capabilities. Custom keyboards are also trivial to disguise as legitimate apps. Since the authentcation happens entirely on-device, a malicious app has direct access to all user interactions and can bypass all hashsing, salts and peppers by collecting secrents before they are processed. None of the current cryptographic implementations help against input interception.

**Mitigation:**  
Detect and warn users about Accessibility Services when the authentication screen is shown. Provide in-app warnings when a non-system IME is being used. Avoid requesting dangerous permissions and encourage good hygiene around installing third-party apps. Use biometric authenticatio (through `KeyStore`) where possible to reduce raw password entry frequently. If feasible, apply `FLAG_SECURE` and obfuscate sensitive fields to reduce UI scraping.

###### In-memory Exposure and Logging
**How:**  
Secrets such as the password security answers may be stored temporarily in memory as immutable `String` objects during authentication. These objects can persist in memory for an undefined period and may appear in heap dumps or memory snapshot, especially of debugging tools, crash reports or analytics frameworks are present. In addition, poorly configured logging statements, stack traces or crash handlers might accidentally log sensitive values.

**Why practical:**  
Java / Kotlin `String` objects cannot be wiped because they are immutable. Without careful handling, secret inputs may remain in the memory heap long after user. Attackers with physical access, root or forensic tools can dump process memory to extract secrets before they are hashed.

**Mitigation:**  
Use `CharArray` or `ByteArray` for handling passwords and reset them after use. Limit the lifetime of sensitive in-memory objects. Disable verbose logging in production builds and ensure no logs contain user secrets. Avoid third-party libraries that capture memory dumps. Use secure input widgets that minimize temporary object creation. Statically analyze code paths to ensure no crash reports leak sensitive data.

###### Side-Channel / Timing / Comparator Attacks
**How:**  
When comparing password hashes or KDF outputs, a naive equality check can leak information through timing differences - e.g. returning early when the first mismatched byte is found. An attacker running code on the same device (e.g. via injected malware, shared resources or precise measurement of system calls) may be able to determine partial correctness of guesses. Over time, they can reduce the search space for [brute-force](#offline-brute-force--dictionary-attack) attempts.

**Why practical:**  
Android devices are shared computational environments: Malicious software can observe execution timing and resource usage. If the implementation uses default `==` or `Array.equals()` for comparison, it may exhibit timing variations. While subtle, this becomes relevant when secrets are stored locally and attackers can make unlimited attempts without network-based rate limiting. Even micro-optimizations help an offline attacker.

**Mitigation:**  
Aways use constant-time comparison methods for secrets, such as `MessageDigest.isEqual()`. Ensure that all password verification operations take approximately the same time regardless of correctness. Add artificial delay or rate-limiting on authentication attempts to further reduce side-channel effectiveness. Avoid revealing whether rejection happened due to wrong number of correct security question answers.

###### Threats to Security Questions
**How:**  
Security questions are usually low-entropy, guessable or discoverable throughh social engineering. An attacker who gains access to stored hashed answers can perform [offline brute-force](#offline-brute-force--dictionary-attack) attacks against the answers, which typically have much smaller key spaces than passwords. Even without technical access, an attacker who knows the user (family name, pet name, birthplace) can answer several questions correctly and recover the master password.

**Why practical:**  
Users overwhelmingly pick predictable answers. Even with salts, peppers and PBKDF2 hashing, the low entropy makes offline attacks feasible. Furthermore, requiring only 4 of 5 answers reduces effecive entropy even more. Since all verification occurs locally, attackers can brute-force each answer independently and combine them. If any hashing errors or timing differences reveal partial correctness, guessing becomes even easier.

**Mitigation:**  
Discourage or prohibit weak questions. Enforce struct complexity requirements for answers (minimum length, random characters or passphrase-style answers). Consider replacing security questions with stronger recovery mechanisms (local recovery codes, biometric authentication tied to `KeyStore` or multi-step recovery requiring device authentication). If security questions remain, apply a hardware-backed pepper in addition to salts.

###### UI and OS Features
**How:**  
Common UI and OS behaviours can leak sensitive information. Screenshots or "Recent Apps" thumbnails can capture the authentication screen. Autofill services might store or suggest passwords. Clipboard contents can be read by other apps of the user copies the password. Notifications or accessibility overlays may reveal partial content. If `FLAG_SECURE` is not used, any app with screenshot permission can capture UI content.

**Why practical:**  
Android's default behaviour is user-friendly, not security-focused. Many apps do not disable screenshots or thumbnails. Autofill frameworks store sensitive entries unless explicitly disabled. Clipboard access is widely available. Attackers with seemingly innocuous permissions or apps running in the background can capture UI content, especially when password fields are displayed without obfuscation.

**Mitigation:**  
Enable `FLAG_SECURE` for authentication screens to block screenshots and "Recent Apps" previews. Disable autofill for sensitive fields. Prevent copy and paste in password fields. Provide secure UI components that mask entirely thouroughly. Use system keyboard only and show warnings when third-party keyboards are active. Avoid showing sensitive content in notifications or overlays.

