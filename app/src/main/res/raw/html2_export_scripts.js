/**
 * Function shows the entry specified by the account ID. This is the ID of the div which contains the entry.
 *
 * @param accountId         ID of the div which contains the account.
 * @param clickedDomElement DOM element that was clicked to show the account.
 */
function showEntry(accountId, clickedDomElement) {
    deselectAllEntries();
    clickedDomElement.classList.add("selected");
    hideAllEntries();
    var element = document.getElementById(accountId);
    element.classList.remove("hidden");
}

/**
 * Function hides all entries by adding the "hidden" class attribute to all entries.
 */
function hideAllEntries() {
    var elements = document.getElementById("content-list").children;
    for (var i = 0; i < elements.length; i++) {
        elements[i].classList.add("hidden");
    }
}

/**
 * Function deselects all entries by removing the "selected" class attribute from all entries.
 */
function deselectAllEntries() {
    var elements = document.getElementById("account-list").children;
    for (var i = 0; i < elements.length; i++) {
        elements[i].classList.remove("selected");
    }
}

/**
 * Function toggles the visibility of a password.
 *
 * @param clickedButton DOM element which was clicked to toggle password visibility.
 */
function togglePasswordVisibility(clickedButton) {
    var parentNode = clickedButton.parentNode;
    var paragraphs = parentNode.querySelectorAll("div > p");
    if (paragraphs.length == 3) {
        if (paragraphs[1].classList.contains("hidden")) {
            paragraphs[1].classList.remove("hidden");
            paragraphs[2].classList.add("hidden");
        }
        else {
            paragraphs[1].classList.add("hidden");
            paragraphs[2].classList.remove("hidden");
        }
    }
}