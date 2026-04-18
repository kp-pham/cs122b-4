const fullTextSearch = $("#full-text");

function handleFullTextSearch(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    let query = $.trim($("input[name=q]").val());

    if (query.length === 0) {
        return;
    }

    window.location.href = "list.html?q=" + query;
}

fullTextSearch.submit(handleFullTextSearch);