<img src="../../img/icon.png" height="150" align="right"/>

# RecyclerViewAdapter.GenericHeadlineViewHolder
```java
public static class RecyclerViewAdapter.GenericHeadlineViewHolder extends RecyclerView.ViewHolder
```
View holder for the generic headline within pages.

###### Table of Contents
1. [Overview](#overview)
2. [Public Fields](#public-fields)
3. [Public Constructors](#public-constructors)

<br/>

## Overview
Create a new view holder of this type as follows:
```java
@Override
@NonNull
public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    switch (viewType) {
        case TYPE_GENERIC_HEADLINE:
            View itemView = layoutInflater.inflate(R.layout.item_generic_headline, parent, false)
            return new GenericHeadlineViewHolder(itemView);
        // ...
    }
}
```

An example for an inflated and updated headline might be as follows:
<div align="center">
    <img src="../../img/development/api/recyclerviewadapter_genericheadlineviewholder_1.png" height="128"/>
</div>

<br/>

## Summary
###### Public Fields
Field | Description
--- | ---
[`headlineTextView`](#headlinetextview) | Text view displaying the text of the headline.
[`dividerView`](#dividerview) | View displaying a divider at the top of the headline.

###### Public Constructors
Constructor | Description
--- | ---
[`GenericHeadlineViewHolder(View)`](#genericheadlineviewholder) | Constructor to create a new view holder from the passed view.

<br/>

***

## headlineTextView
```java
public final TextView headlineTextView;
```
Text view displaying the text of the headline.

<br/>

## dividerView
```java
public final View dividerView;
```
View displaying a divider at the top of the headline.

<br/>

***

## GenericHeadlineViewHolder
```
public GenericHeadlineViewHolder(View itemView)
```
Constructor instantiates a new view holder from the inflated view that is passed as parameter.

###### Parameters
Parameter | Description
--- | ---
`itemView` | Inflated view from which to create the view holder.

<br/>

***
2024-08-25  
&copy; Christian-2003
