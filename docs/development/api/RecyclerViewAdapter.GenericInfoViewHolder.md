<img src="../../img/icon.png" height="150" align="right"/>

# RecyclerViewAdapter.GenericInfoViewHolder
```java
public static class RecyclerViewAdapter.GenericInfoViewHolder extends RecyclerView.ViewHolder
```
View holder for the generic info item within pages.

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
        case TYPE_GENERIC_INFO:
            View itemView = layoutInflater.inflate(R.layout.item_generic_info, parent, false)
            return new GenericInfoViewHolder(itemView);
        // ...
    }
}
```

An example for an inflated and updated info item might be as follows:
<div align="center">
    <img src="../../img/development/api/recyclerviewadapter_genericinfoviewholder_1.png" height="128"/>
</div>

<br/>

## Summary
###### Public Fields
Field | Description
--- | ---
[`infoTextView`](#infotextview) | Text view displaying the info text of the item.

###### Public Constructors
Constructor | Description
--- | ---
[`GenericInfoViewHolder(View)`](#genericinfoviewholder) | Constructor to create a new view holder from the passed view.

<br/>

***

## infoTextView
```java
public final TextView infoTextView;
```
Text view displaying the info text of the item.

<br/>

***

## GenericInfoViewHolder
```java
public GenericInfoViewHolder(View itemView)
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
