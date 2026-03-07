<img src="../img/icon.png" height="150" align="right">

# Module :feature:search
The module :feature:search module contains the entire functionality for the search feature within the app Password Vault.

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
    * [10.2 Accuracy (Q02)](#102-accuracy-q02)
    * [10.3 Performance (Q03)](#103-performance-q03)
* [11 Risks and Technical Debt](#11-risks-and-technical-debt)
    * [11.1 Interactions with Other Modules](#111-interactions-with-other-modules)
    * [11.2 Concurrency](#112-concurrency)
* [12 Glossary](#12-glossary)

<br/>

## 1 Introduction and Goals
This document describes the search feature, which allows the user to search through data within the app Password Vault. It can take a simple search query and return a list of accounts that match the search query.

For users that store large numbers of accounts within the app, a search feature is important to quickly find desired accounts.

Search queries are simple strings that are searched within accounts and account details. More complex queries that search for specific information in specific fields are allowed as well, which can be used by power users. The feature employs a lightweight query-language that power users are familiar with from other applications, such as GitHub, Gmail or Outlook.

The following goals have been established for the feature:

ID | Goal
--- | ---
G01 | The feature shall support simple free-text searches like "Netflix". This should find a list of accounts that mention "Netflix" anywhere.
G02 | The feature shall support a lightweight query language in the format "&lt;field&gt;:&lt;value&gt;", like "name:Netflix". This should find all accounts that mention "Netflix" within their name.
G03 | The query language shall support simple relational operators for dates, such as `<, >, <=, >=, <>`. These operators can be used to further filter accounts and their details, based on their metadata dates, such as "editedAt" or "createdAt". Example: The query `editedAt:>2025-06-22` should find all accounts that are edited after the 2025-06-22.
G04 | The user interface for this feature shall provide simple filter mechanisms that allow users to build simple queries without knowing the syntax for the query language. Examples are filter chips that allow the selection for tags by which to filter.
G05 | The system shall be extensible, so that further data can be added to the search operations in the future. This specifically applies to the files-feature that is intended to be added later.

<br/>

### 1.1 Functional Requirements
In order to satisfy the user, the following functional requirements can be established:

ID | Requirement | Description
--- | --- | ---
F01 | Intuitive query input | The search query (both for free-text search as well as for the lightweight query language) should be provided through the same search field. The system has to decide whether the input is a free-text search or needs to be parsed as input for the query language.
F02 | Find accounts | Accounts that match the provided search query should be retrieved and displayed to the user. If some account details match the provided query, they shall be displayed as well. For example, the user searches for an address like "Platz der Republik 1, 11011 Berlin", the system should display the accounts that contain a detail with this address.
F03 | Landing page | Once the search feature is shown to the user, a landing page shall be displayed that displays controls through which aid in search operations.
F04 | Recent queries | The system shall remember multiple recent search queries that are displayed to the user on the landing page. These queries can be clicked and they are entered in the search field.
F05 | UI filters | The landing page shall display filters that aid the user in filtering the data. This way, regular users do not need to know about the query language. Example filters include (1) filter by tags, (2) filter by created date and (3) filter by edited date.

<br/>

### 1.2 Quality Requirements
The following quality requirements can be established:

ID | Requirement | Description
--- | --- | ---
[Q01](#101-ease-of-use-q01) | Ease of use | The query language shall be designed in a way that makes it easy to understand. Preferrably, the language is similar to existing query language that power users are familiar with.
[Q02](#102-accuracy-q02) | Accuracy | The results of a search operation shall match the provided query. Users shall be able to understand why data was found by their query. Ideally, the query finds all real matches and displays them accordingly.
[Q03](#103-performance-q03) | Performance | The search operation shall be performed in a suitable amount of time.

<br/>

## 2 Constraints
The following constraints are established:

* Integrated with the Gradle build tool
* Dependencies are managed with version catalog

<br/>

## 3 Context and Scope
The following section describes the context and scope for the search feature.

<br/>

### 3.1 Business Context
The following illustration shows the business context for the search feature:

![Business Context](./../img/architecture/module%20feature-search/business_context.drawio.svg)

<br/>

### 3.2 Technical Context
The search feature is accesses through the public API by the :app module, which integrates the module seamlessly into the existing user interface. Furthermore, the :app-module provides integration with other modules, such as the :feature:accounts module in order to show account information once a search result is selected by the user.

The module depends on all :core-modules in order to access common business logic.

Additionally, the module depends on the :data:accounts-module. The :data:accounts-module is required to get account information for search operations.

The following diagram illustrates these dependencies:

![Technical Context](./../img/architecture/module%20feature-search/technical_context.drawio.svg)

<br/>

## 4 Solution Strategy
The search feature is implemented as an independent module that relies on the :data:accounts-module to get access to account data.

Technically, the search functionality could be amended to the :feature:accounts-module, since that module handles all account-related functionality. However, we intend to extend the app with another feature allowing the user to store files securely within the app. Once added, these files should be regarded by the search operations as well. In this case, the search functionality needs to be extracted to a separate module in order to fulfill the separation of concerns requirement and retain a clean architecture. For this reason, we implement the search feature separately.

Internally, the module handles the following:
* Implementation of the domain layer for the search logic
* Implementation on business logic for search operations. This includes the parsing and evaluation of search queries, as well as the orchestration for the entire search operation
* Interaction with the Android OS and device through an infrastructute layer to remember user configuration
* Presentation of the search results as well as views to start search operations

<br/>

## 5 Building Block View
The following figure shows the top-level building blocks of the module, as well as their subdivisions:

![Building Blocks](./../img/architecture/module%20feature-search/building_blocks.drawio.svg)

<br/>

### 5.1 Scope and Context
The following figure defines the scope and context:

![Building Blocks - Scope and Context](./../img/architecture/module%20feature-search/building_blocks_scopeandcontext.drawio.svg)

The following building blocks can be identified:

Block | Responsibility
--- | ---
User | The user is the primary entity which interacts with the module through the user interface. They dictate the actions to take and expect behaviours to occur as a result.
:app | The :app-module is responsible for the seamless integration of the :feature:search-module within the entire app.
:data:accounts | The :data:accounts-module is reponsible for providing access to the account data required for search operations.
Android | The Android OS provides capabilities for storage that are required for the user configuration.

<br/>

### 5.2 Level 1
The following figure defines the scope and context:

![Building Blocks - Level 1](./../img/architecture/module%20feature-search/building_blocks_level1.drawio.svg)

The following building blocks can be identified:

Block | Parent | Responsibility
--- | --- | ---
User config | :feature:search | The user config is responsible for storing and retrieving the user configuration, such as a list of the most recent queries.
User interface | :feature:search | The user interface is the primary surface through which the user interacts with the module. It provides input methods through which to query data. After running a search operation, the interface shows the results of the operation.
Search use case | :feature:search | The search use case takes a search query (either for free-text search or for the lightweight query language) and orchestrates the entire search logic. More complex operations (such as querying data or query parsing) are moved to other blocks, with which the use case interacts.
Query parser | :feature:search | The query parser handles the parsing and evaluation of search queries.

<br/>

### 5.3 Level 2
The following figure defines the scope and context:

![Building Blocks - Level 2](./../img/architecture/module%20feature-search/building_blocks_level2.drawio.svg)

The following building blocks can be identified:

Block | Parent | Responsibility
--- | --- | ---
Repository | User config | Repository interface allows the module to access the user configuration.
Concrete repository | User config | The concrete implementation for the repository handles storing data. For this purpose, the `SharedPreferences`-system of the Android OS is used.
SearchViewModel | User interface | The view model mediates between the view (SearchScreen) and the business logic / domain model of the module. It interacts with the user config in order to utilize the configuration for UX convenience. Furthermore, the view model invokes the search use case in order to perform a search operation with a query entered through the SearchScreen.
SearchScreen | User interface | The SearchScreen represents the primary interface through which the user interacts. It is integrated within the :app-module. The screen allows the user to enter a search query.
FilterView | User interface | The FilterView is displayed before any search operation runs. The filter view provides useful filters as well as a list of recent queries.
ResultView | User interface | After running a search operation, the ResultsView displays the results of the search operation.
QueryTokenizer | Query parser | The tokenizer tokenizes a search query into individual tokens, such as ["name", ":", "Netflix"] for "name:Netflix".
QueryParser | Query parser | The parser parses a list of tokens into an abstract syntax tree (AST) that can be used for further evaluation.
QueryEvaluator | Query parser | Interface provides capabilities to evaluate whether some input data matches an AST.
QueryAccountEvaluator | Query parser | Concrete implementation of QueryEvaluator to check whether an account (without it's details) matches a query AST.
QueryDetailEvaluator | Query parser | Concrete implementation of QueryEvaluator to check whether an account detail matches a query AST.

<br/>

## 6 Runtime View
The architecture documentation focuses on the main scenario of this module, which is the orchestration of a search operation. The module has other components as well, such as the user config. These other modules are disregarded in this section for brevity and in order to focus on the main feature.

The key steps for search operations are:
1. Search query is entered by the user
2. Query is parsed
3. Data is loaded and evaluated
4. Matching data is displayed to the user

The following UML sequence diagram displays the internal sequences for a search operation:

![Runtime View Sequence](./../img/architecture/module%20feature-search/runtime_view_sequence.drawio.svg)

<br/>

## 7 Deployment View
The :feature:search-module is deployed within the Password Vault application, which needs to be installed on a device running Android OS.

Inside Password Vault, :feature:search is integrated within the existing module architecture.

![Deployment View](./../img/architecture/module%20feature-search/deployment_view.drawio.svg)

<br/>

## 8 Crosscutting Concerns
This section describes the crosscutting concerns for the module.

<br/>

### 8.1 Domain Concepts
The domain model for this module is rather simple and illustrated by the following UML class diagram:

![Domain Concepts](./../img/architecture/module%20feature-search/domain_concepts.drawio.svg)

The domain model consists of two distinct parts: The model inside the :feature:search-module, as well as parts of the public domain model of :data:accounts.

The public domain model of :data:accounts is required to model the actual account information through which the search feature determines matches for a search query. Mainly, this consists of the following classes:
* `AccountDescriptor`: A small class containing only the most basic information about an account, without the actual account information (i.e. details).
* `Detail`: Class stores actual account information, such as email addresses, passwords or usernames.

These classes are required for the final search result which is returned after a search opertion. During the search operation, the business logic of :feature:search accesses additional classes from the public domain model of :data:accounts, which are omitted from the diagram above.

The domain model from :feature:search is entirely internal and not exposed to other modules. It consists of two parts which are independent from each other:
* Query parsing model:
    * `QueryToken`: This class models a query token.
    * `QueryTokenType`: This enum contains the types for query tokens.
    `QueryTokenCollection`: A helper class used to store a collection of tokens generated during tokenization. The class provides access methods that are later required by the parser.
    * `QueryAstNode`: Node for an AST which consists of a node token as well as two subtree nodes.
* Search result model:
    * `SearchResult`: Models the search result returned by the search operation.
    * `AccountSearchResult`: Single search result item for an account. Each item models exactly one account and contains the respective account descriptor as well as a list of details that match the search query.

Some may regard the query parsing model as DTOs, which is correct technically. However, since a large part of this module is concerned with the parsing of seach queries, we decided to implement this as domain model.

<br/>

### 8.2 Patterns
The implementation shall utilize common programming, design and architecture patterns.

The following patterns are utilized:
* MVVM
* SOLID
* DRY
* Clean Architecture

The following diagram illustrates the implementation of clean architecture:

![Architecture](./../img/architecture/module%20feature-search/architecture.drawio.svg)

<br/>

## 9 Architectural Decisions
This section outlines and explains important architectural decisions.

<br/>

### 9.1 Package Structure
The package structure reflects the architectural and design patterns, such as clean architecture and MVVM. The following package structure applies:
```
de.christian2003.feature.search
+-- domain
|   +-- entities
|   +-- repositories
|
+-- application
|   +-- services
|   +-- usecases
|
+-- infrastructure
|   +-- repositories
|
+-- presentation
|   +-- models
|   +-- ui
|   |   +-- search
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
`fun NavGraphBuilder.searchDestination(...)` | `navigation` | Extension method for `NavGraphBuilder` is used to insert the internal `SearchScreen` into the Jetpack Compose navigation from the :app-module.
`object SearchDestination` | `navigation` | Route object for the navigation destination mentioned above.

<br/>

### 9.3 Dependencies
The following dependencies are required by the module:

Dependency | Usage
--- | ---
`:core:common` | Required for formatting of dates and times.
`:core:ui` | Required for common UI.
`:data:accounts` | Required to fetch accounts for search operations.

<br/>

## 10 Quality Requirements
This section contains all quality requirements that need to be addressed by the feature.

<br/>

### 10.1 Ease of Use (Q01)
The query language is designed in a way that is familiar to power users. Furthermore, the query language syntax is simple and intuitive.

Generally and for the most purposes, the syntax is simply `<field>:<value>` with an optional relational operator for fields that support relational equality (`<field>:<relational-operator><value>`).

Examples for the query language syntax:

Query | Description
--- | ---
name:Netflix | Finds all accounts whose name contains "Netflix".
name:"Password Manager" | Finds all accounts whose name contains "Password Vault". Quotation marks are required because the literal "Password Vault" contains a space.
tag:&lt;&gt;Work | Finds all accounts that do not contain the tag "Work".
createdAt:&lt;2025-06-22 | Finds all accounts that were created before 2025-06-22.

The requirement is fulfilled easily after the query language has been designed, by implementing an interpreter that complies with the language definition.

<br/>

### 10.2 Accuracy (Q02)
Search operations shall find all data that match the search query.

In order to fulfill this requirement, all accounts need to be queried and all fields need to be searched. The following fields need to be searched:

* Accounts
    * Name
    * Description
    * CreatedAt date (metadata)
    * EditedAt date (metadata)
* Details
    * Name
    * Content
    * CreatedAt date (metadata)
    * EditedAt date (metadata)
* Targets
    * Package name (e.g. "de.christian2003.petrolindex")
    * Localized package name queried from the Android OS (e.g. "Petrol Index")
* Tags
    * Name

By correctly querying all accounts and their data, this requirement can be fulfilled easily by searching the fields listed above.

<br/>

### 10.3 Performance (Q03)
Search operations shall be performed in a timely manner. Operations should not take longer than necessary. This requirement is more difficult to fulfill, since smartphones are generally less powerful than regular computers.

The main performance impacts are as follows:
* Parsing of complex queries
* Evaluating of complex queries
* Loading large numbers of accounts

Parsing and evaluating complex queries takes some time. However, this time cannot be reduced meaningfully, since it is strictly required.

Loading large numbers of accounts facilitates a much larger impact on performance than the parsing and evaluation of complex queries. Loading _n_ accounts requires _n + 1_ requests to the database, which internally translates to about _3 * n + 1_ IO operations. These IO opeations are a heavy performance impact.

The following solutions are proposed to mitigate these performance impacts:
* **Limit the number of IO operations:** This has to be implemented in the :data:accounts-module, so this is out of scope for this document.
* **Multithreading:** For evaluating (and loading) each account, a new coroutine can be started. 

<br/>

## 11 Risks and Technical Debt
This section outlines the risks and technical debt associated with the module.

<br/>

### 11.1 Interactions with Other Modules
Functionality of other modules are usually provided through their own use case classes, such as `GetAllAccountsUseCase` or `GetAccountByIdUseCase`. These use cases can be subject to change.

Ideally, calling these use cases would be abstracted through an infrastructure-interface within this module as follows:
```
+-----------------------+        +-------------+    +---------------+    +-----------+    +--------+
| GetAllAccountsUseCase |   <-   | AccountsApi | <- | SearchUseCase | <- | ViewModel | <- | Screen |
+-----------------------+        +-------------+    +---------------+    +-----------+    +--------+

:data:accounts                   :feature:search
```

However, this module calls these foreign use cases immediately without any abstraction. Changes in the use cases of other modules require changes to this module as well. However, assuming that we have full control over the app, we can keep changes to use cases to a minimum.

<br/>

### 11.2 Concurrency
Due to large numbers of data being searched, concurrency should be utilized to improve performance. This has been named as a requirement specifically ([10.3 Performance (Q03)](#103-performance-q03)).

However, no concurrency is employed at the moment to improve performance. Once the need arises, concurrency can be implemented in `SearchUseCase` to improve performance.

<br/>

## 12 Glossary
The following terms and abbreviations are used in this document:

Term | Description
--- | ---
AST | Abstract syntax tree
DI | Dependency Injection
DRY | Dont repeat yourself
DTO | Data transfer object
MVVM | Model View ViewModel
SOLID | Single Responsibility, Open-Closed, Liskov Substitution, Interface Segregation and Dependency Inversion

<br/>

***
2026-03-07  
&copy; Christian-2003
