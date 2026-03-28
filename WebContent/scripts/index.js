function handleResult(resultData) {
    let genresList = $("#genres-list");

    resultData.forEach((genre, index) => {
        let link = `<a href="list.html?genre=${encodeURIComponent(genre)}" class="text-center">${genre}</a>`;
        genresList.append(link);
    });
}

function showPrefixes() {
    let prefixList = $("#prefix-list");
    let prefixes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789*";

    for (const prefix of prefixes) {
        let link = `<a href="list.html?prefix=${encodeURIComponent(prefix)}">${prefix}</a>`;
        prefixList.append(link);
    }
}

showPrefixes();

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/genres",
    success: (resultData) => handleResult(resultData)
});