const fullTextSearch = $("#full-text");

$("#autocomplete").autocomplete({
    lookup: (query, done) => handleLookup(query, done),
    onSelect: (suggestion) => handleSelectSuggestion(suggestion),
    deferRequestBy: 300,
    minChars: 3,
    triggerSelectOnValidInput: false,
    noCache: true,
});

function handleFullTextSearch(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    let query = $.trim($("input[name=q]").val());

    if (query.length === 0) {
        return;
    }

    window.location.href = "list.html?q=" + query;
}

fullTextSearch.submit(handleFullTextSearch);