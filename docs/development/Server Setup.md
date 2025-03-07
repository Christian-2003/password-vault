<img src="../img/icon.png" height="150" align="right"/>

# Server Setup
Password Vault contains some limited (and optional) features that make the app interact with the website [passwordvault.christian2003.de](https://passwordvault.christian2003.de). Since the server is not a part of this repository, this document contains information about the server-side setup that is required for Password Vault.

###### Table of Contents
1. [App Links](#app-links)
2. [Help and Legal Pages](#help-and-legal-pages)

<br/>

## App Links
Opening a link of a specific format (e.g. `https://passwordvault.christian2003.de/data/quality_gate`) on an Android device should not open the website referred, but instead the Password Vault app. This requires a specific setup within the manifest. However, an additional server-side setup is required.

###### Assetlinks
The server needs to contain a file `assetlinks.json` available at the following URL: [https://passwordvault.christian2003.de/.well-known/assetlinks.json](https://passwordvault.christian2003.de/.well-known/assetlinks.json).

This file must contain the following code:
```json
[
  {
    "relation": ["delegate_permission/common.handle_all_urls"],
    "target": {
      "namespace": "android_app",
      "package_name": "de.christian2003.passwordvault",
      "sha256_cert_fingerprints": [
        "07:FA:28:97:BF:25:3A:8E:ED:56:C1:F4:4A:3B:E6:FE:19:18:39:D5:26:9D:41:D1:75:2F:A1:B8:09:DB:15:76"
      ]
    }
  }
]
```

<br/>

## Help and Legal Pages
The app accesses a REST API to get information about available help and legal pages in different languages.

###### Help Pages
The REST endpoint to get information about help pages is available at the following URL [https://api.passwordvault.christian2003.de/rest/help.json](https://api.passwordvault.christian2003.de/rest/help.json).

This file must contain the following code:
```json
{
  "version": 1,
  "tutorials": [
    {
      "pages": [
        {
          "lang": "en",
          "title": "Quickstart Guide",
          "url": "https://passwordvault.christian2003.de/en/blog/quickstart/"
        },
        {
          "lang": "de",
          "title": "Schnellstart",
          "url": "https://passwordvault.christian2003.de/de/blog/quickstart/"
        }
      ]
    },
    //Put other help pages into this array.
  ]
}
```

###### Terms of Service
The REST endpoint to get information about the terms of service is available at the following URL [https://api.passwordvault.christian2003.de/rest/tos.json](https://api.passwordvault.christian2003.de/rest/tos.json).

This file must contain the following code:
```json
{
  "version": 3,
  "valid": "2025-02-08",
  "pages": [
    {
      "lang": "en",
      "title": "Terms of Service",
      "url": "https://passwordvault.christian2003.de/en/legal/tos/"
    },
    {
      "lang": "de",
      "title": "Nutzungsbedingungen",
      "url": "https://passwordvault.christian2003.de/de/legal/tos/"
    }
  ]
}
```

If the version code is incremented, the user is notified about changes to the terms of service.

###### Privacy Policy
The REST endpoint to get information about the privacy policy is available at the following URL [https://api.passwordvault.christian2003.de/rest/privacy.json](https://api.passwordvault.christian2003.de/rest/privacy.json).

This file must contain the following code:
```json
{
  "version": 3,
  "valid": "2025-02-08",
  "pages": [
    {
      "lang": "en",
      "title": "Privacy Policy",
      "url": "https://passwordvault.christian2003.de/en/legal/privacy/"
    },
    {
      "lang": "de",
      "title": "Datenschutzerklärung",
      "url": "https://passwordvault.christian2003.de/de/legal/privacy/"
    }
  ]
}
```

If the version code is incremented, the user is notified about changes to the privacy policy.

<br/>

***
2025-02-18  
&copy; Christian-2003
