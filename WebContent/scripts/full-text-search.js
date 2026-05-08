const fullTextSearch = $("#full-text");

function handleLookup(query, done) {
    console.log("Autocomplete search initiated: ", query);

    const cached = sessionStorage.getItem(query);

    if (cached) {
        console.log("");

        const suggestions = JSON.parse(cached);

        console.log("Cached suggestion list: ", suggestions);

        done({ suggestions: suggestions });

        return;
    }
}

$("#autocomplete").autocomplete({
    lookup: (query, done) => handleLookup(query, done),
    // onSelect: (suggestion) => handleSelectSuggestion(suggestion),
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