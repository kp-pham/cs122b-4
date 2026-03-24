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

function handleResult(resultData) {
    $('title').text(resultData["name"]);

    let starInfo = jQuery("#star_info");

    starInfo.append("<p>Star Name: " + resultData["name"] + "</p>" +
                    "<p>Data of Birth: " + resultData["birthYear"] + "</p>");

    let movieTable = jQuery("#movie_table_body");

    resultData["movies"].forEach(movie => {
        let row = `
            <tr>
                <td>${movie["title"]}</td>
                <td>${movie["year"]}</td>
                <td>${movie["director"]}</td>
            </tr>
        `;

        movieTable.append(row);
    });
}

let starId = getParameterByName('id');

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-star?id=" + starId,
    success: (resultData) => handleResult(resultData)
});