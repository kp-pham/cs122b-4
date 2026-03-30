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
    let movieTable = jQuery("#movie-table-body");

    resultData.forEach(movie => {
        let row = `
            <tr>
                <td>
                    <a href="single-movie.html?id=${movie['id']}">${movie['title']}</a>
                </td>
                <td>${movie['year']}</td>
                <td>${movie['director']}</td>
                <td>${movie['genres'].slice(0, 3).join(', ')}</td>
                <td>
                    ${movie['stars'].slice(0, 3).map(({id, name}) => {
            return `<a href="single-star.html?id=${id}">${name}</a>`
        }).join(', ')}
                </td>
                <td>${(movie['rating'] ?? "N/A")}</td>
                <td>
                    <form class="cart-form" method="POST" action="#">
                        <input type="hidden" name="id" value="${movie['id']}">
                        <button type="submit" class="rounded p-2 text-white bg-dark">Add</button>
                    </form>
                </td>
            </tr>
        `;

        movieTable.append(row);
    });
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

    return "api/search?" + params.join("&");
}

function submitCartForm(submitFormEvent) {
    submitFormEvent.preventDefault();

    let id = $(this).find("input[name='id']").val();

    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: `api/cart?action=add&id=${encodeURIComponent(id)}`
    });
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: buildUrl(),
    success: (resultData) => handleResult(resultData)
});

$(document).on("submit", ".cart-form", submitCartForm);