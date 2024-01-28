<img src="../img/icon.png" height="150" align="right">

# Add new Documentation Files
This document describes the process of adding new documentation files to the project and is aimed at developers to better understand the process of adding such files.

###### Table of Contents
1. [Adding Development Documentation Files](#adding-development-documentation-files)
    1. [What to Document](#what-to-document)
    2. [What Not to Document](#what-not-to-document)
    3. [Where to Store Documentation Files](#where-to-store-documentation-files)
2. [Adding Manual Files](#adding-manual-files)
    1. [What to Document in Manuals](#what-to-document-in-manuals)
    2. [How to Link Between Manuals](#how-to-link-between-manuals)
    3. [Where to Store Manuals](#where-to-store-manuals)
3. [Design Guidelines](#design-guidelines)
    1. [Document Title](#document-title)
    2. [Headlines](#headlines)
    3. [Top of The Document](#top-of-the-document)
4. [Links Between Documents](#links-between-documents)
5. [Images](#images)

<br>

## Adding Development Documentation Files
Adding documentation files for development is an important task, as these files will be used for further development processes. Having a uniform and clear structure to those files aids the development process and makes life a lot easier.

###### What to Document
Everything that appears to be complicated should be documented. This could include data encodings, application processes and much more.

If required, add pictures or diagrams to the documentation. This makes it a lot easier to understand certain structures and processes. Refer to the guidelines regarding [imgages](#images) for further information about using pictures.

###### What Not to Document
Please avoid documenting the source code within additional documentation files. Source code should be documented within the respective source files. This allows the documentation to be available within the IDE, which makes the development a lot easier.

###### Where to Store Documentation Files
Important documentation files should always be stored within [docs/development/](../development/). Less important documentation files should be stored within subdirectories of the previously mentioned folder.

<br>

## Adding Manual Files
Manual files are documentation files that are aimed at users of the application. Therefore, it is much more important to preserve a certain quality with these files.

Manual files should be explicitly detailed in every possible way to make sure that every possible user understands them.

###### What to Document in Manuals
Manuals should only be created to explain certain processes to the user within the scope of the app. They should not require the user to be proficient in virtually anything, especially not computer science.

###### How to Link Between Manuals 
Please do not require the user to navigate through GitHub by themselves. Instead, use relative links to link between files. Unlike with development documentation, do not include the directory within the link name itself. Prefer a description of the file to which you are linking.

Please do not use links like this:  
`Read about this in the manual: [docs/manual/Update to Version 3.2.0.md](../manual/Update%20to%20Version%203.2.0.md).`

Instead, prefer links like this:  
`Read about this in the [manual](../manual/Update%20to%20Version%203.2.0.md).`

Furthermore, do not require the user to enter GitHub themselves to see a manual. If you have a reason to create a manual, you should always refer to the manual in suitable situations from pages with which the user interacts. For example, do not require the user to look up update instructions on this repository by themselves. Instead, link to update instructions from the description of update releases.

###### Where to Store Manuals
Unlike the development documentation, the user is not required to navigate the repository to find the manuals. Therefore, a useable and easy-to-navigate directory structure is much less important. However, please keep in mind that developers still need to be able to navigate through the manuals.

Always keep manuals within [docs/manual/](../manual/) and subdirectories.

<br>

## Design Guidelines
To keep documentation files as visually pleasing as is possible with markdown, please follow the design guidelines.

###### Document Title
Please provide the document with a suitable title which explains exactly what the document does. Try to keep the title as short as possible.

Do **not** try to avoid spaces within document names! Use spaces as is appropriate. Consider the name of this document for guideline regarding spaces in titles: `Add New Documentation Files.md`.

###### Headlines
Structure your document through headlines. Keep it simple and try to avoid using all six headline levels. Instead, only use `<h1>`, `<h2>` and `<h6>` in the following scenarios:

Use `<h1>` for the first headline within your document. The content of this headline should be the title of the document.

Afterwards, only use `<h2>` for all headlines (e.g. for chapters). Between `<h2>`-headlines, try to only use `<h6>` headlines.

To get an idea of how this works, look at the structure of this document:
```markdown
# Add New Documentation Files
...

###### Table of Contents
...

## Adding Development Documentation Files
...

###### What to Document
...

###### What Not to Document
...

###### Where to Store Documentation Files
...

## Adding Manual Files
...

###### What to Document in Manuals
```

Please try to only use `<h6>`-headlines within `<h2>`-headlines if there are more than one of those `<h6>`-headlines. Try to avoid using them if you only need one.

Before every `<h2>`-headline, add a `<br>`-element to further visually divide the next chapter:
```markdown
## First H2 headline
...

<br>

## Second H2 headline
...
```

###### Top of The Document
At the top of the document, add the PasswordVault logo to the right hand side. This can be done by using the following HTML in the first line of the markdown file:
```html
<img src="../img/icon.png" height="150" align="right">
```
Replace `../img/icon.png` with the relative path to the PasswordVault icon.

Beneath this, add the main `<h1>`-headline which includes the document title. If you got some important badges for the document, you could also show them there.

Beneath the headline, write a short description of the document and what you aim to do within this document. This is the most important part of the document, since this will decide whether the reader will continue reading through your document. Be as concise but detailed as possible and provide reasons on why the user should continue reading.

If you think that your document is large enough, you can add a table of contents as well.

An example for a top of the document might be as follows:

```markdown
<img src="../img/icon.png" height="150" align="right">

# Add New Documentation Files
This document describes the process of adding new documentation files to the project and is aimed at developers to better understand the process of adding such files.

###### Table of Contents
1. [Adding Development Documentation Files](#adding-development-documentation-files)
    1. [What to Document](#what-to-document)
    2. [What Not to Document](#what-not-to-document)
2. [Adding Manual Files](#adding-manual-files)
    1. [What to Document in Manuals](#what-to-document-in-manuals)
    2. [How to Link Between Manuals](#how-to-link-between-manuals)

<br>

## Adding Development Documentation Files
...
```

<br>

## Links Between Documents
If possible, add links to other documents the first time they are mentioned within a specific context. But try to limit links as possible, as having too many links complicates reading a document and makes the reader feel overwhelmed.

If you link to other documents within the repository, always use relative links!

<br>

## Images
Use images whenever you feel it is necessary.

Store important images, like the app icon within [docs/img/](../img/). Less important images can be stored within subdirectories of the previously mentioned directory.

Include images using the default markdown syntax whenever possible, e.g.:
```markdown
![PasswordVault](../img/icon.png)
```

If you feel that an image is too large, use the HTML-method instead. With this method, provide height or width constraints to make the image smaller within your document, e.g.:
```html
<img src="../img/icon.png" height="150">
```
When doing this, it might be advisable to add alignment to the image as well:
```html
<img src="../img/icon.png" height="150" align="center">
```
