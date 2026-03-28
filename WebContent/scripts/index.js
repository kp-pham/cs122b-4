function handleResult(resultData) {
    let genresList = $("#genres-list");

    resultData.forEach((genre, index) => {
        let link = `<a href="list.html?genre=${encodeURIComponent(genre)}"></a>`;
        genresList.append(link);
    });
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/genres",
    success: (resultData) => handleResult(resultData)
});