const fullTextSearch = $("#full-text");

function handleFullTextSearch(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    let title = $.trim($("input[name=title]").val());

    if (title.length === 0) {
        return;
    }

    window.location.href = "list.html?title=" + title;
}

fullTextSearch.submit(handleFullTextSearch);