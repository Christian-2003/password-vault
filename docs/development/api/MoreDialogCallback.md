<img src="../../img/icon.png" height="150" align="right"/>

# MoreDialogCallback
```java
public interface MoreDialogCallback
```
Class implements the callback for a [`MoreDialog`](MoreDialog.md). This callback is invoked on a host whenever a context action of the MoreDialog is triggered.

###### Table of Contents
1. [Overview](#overview)
2. [Public Methods](#public-methods)

<br/>

## Overview
This interface must be implemented by the host (e.g. an activity) of a MoreDiaog:
```java
public class MyActivity extends PasswordVaultActivity<MyViewModel> implements MoreDialogCallback {

    //...

    //Handle callbacks from the dialog:
    public void onDialogItemClicked(MoreDialog dialog, String tag, int position) {
        switch(tag) {
            case "tag_edit":
                //Handle context action...
                break;
            case "tag_delete":
                //Handle context action...
                break;
            case "tag_disable":
                //Handle context action...
                break;
            case "tag_share":
                //Handle context action...
                break;
        }
    }

}
```

<br/>

***

## Summary

###### Public Methods
Method | Description
--- | ---
[`onDialogItemClicked(MoreDialog, String, int)`](#onDialogItemClicked) | Method is called whenever a context action of a MoreDialog is invoked.

<br/>

***

## onDialogItemClicked
```java
void onDialogItemClicked(MoreDialog dialog, String tag, int position);
```
Method is called whenever a context action of a MoreDialog is invoked.

###### Parameters
Parameter | Description
--- | ---
`dialog` | Dialog in which the context action was triggered.
`tag` | Tag of the context action.
`position` | Position of the context action within the MoreDialog.

<br/>

***

2025-01-12  
&copy; Christian-2003
