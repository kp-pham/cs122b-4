function getParameterByName(target) {
    // Retrieve URL from browser window
    let url = window.location.href;

    //Escape special characters
    target = target.replace(/[\[\]]/g, "\\$&");

    // Build and execute regular expression
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)");
    let results = regex.exec(url);

    // Handle no matches and empty values
    if (!results) return null;
    if (!results[2]) return '';

    return decodeURIComponent(results[2]);
}

function buildUrl() {
    let genre = getParameterByName("genre");
    let prefix = getParameterByName("prefix");

    return (genre != null) ? `api/browse?genre=${genre}` : `api/browse?prefix=${prefix}`;
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: buildUrl(),
    success: (resultData) => handleResult(resultData)
});