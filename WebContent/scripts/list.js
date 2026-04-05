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
                        <button type="submit" class="rounded text-white bg-dark">Add</button>
                    </form>
                </td>
            </tr>
        `;

        movieTable.append(row);
    });
}

function buildUrl() {
    let genre = getParameterByName("genre");
    let prefix = getParameterByName("prefix");
    let sort = getParameterByName("sort") || "title-asc-rating-desc";

    return (genre != null) ? `api/browse?genre=${encodeURIComponent(genre)}&sort=${encodeURIComponent(sort)}`
        : `api/browse?prefix=${encodeURIComponent(prefix)}&sort=${encodeURIComponent(sort)}`;
}

function isValid(state) {
    return state && (state.type === "browse" || state.type === "search");
}

function showResults() {
    let state = JSON.parse(sessionStorage.getItem("movieListState"));

    if (isValid(state)) {

    } else {

    }
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

$("#options-form").submit(submitOptionsForm);
$(document).on("submit", ".cart-form", submitCartForm);