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
    $("title").text(resultData["title"]);

    let movieInfo = jQuery("#movie_info");

    movieInfo.append(`<p>${resultData['title']} (${resultData['year']})</p>`);

    let movieTable = jQuery("#movie_table");

    let row = `
        <tr>
            <td>${resultData['director']}</td>
            <td>${resultData['genres']}</td>
            <td>
                ${resultData['stars'].map(({id, name}) => {
                    return `<a href="single-star.html?id=${id}">${name}</a>`
                }).join(', ')}
            </td>
        </tr>
    `;

    movieTable.append(row);
}

let movieId = getParameterByName('id')

// 404 Page for failure?
jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/single-movie?id=" + movieId,
    success: (resultData) => handleResult(resultData)
})