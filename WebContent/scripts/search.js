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
    let params = [];

    let title = getParameterByName("title");
    let year = getParameterByName("year");
    let director = getParameterByName("director");
    let star = getParameterByName("star");

    if (title != null && title.length !== 0) {
        params.push(`title=${encodeURIComponent(title)}`);
    }

    if (year != null && year.length !== 0) {
        params.push(`year=${encodeURIComponent(year)}`);
    }

    if (director != null && director.length !== 0) {
        params.push(`director=${encodeURIComponent(director)}`);
    }

    if (star != null && star.length !== 0) {
        params.push(`star=${encodeURIComponent(star)}`);
    }

    return "api/search?" + params.join("&")
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: buildUrl(),
    success: (resultData) => handleResult(resultData)
});