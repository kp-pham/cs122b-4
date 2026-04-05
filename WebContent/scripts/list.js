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

    if (!isValid(state)) {
        const genre = getParameterByName("genre");
        const prefix = getParameterByName("prefix");
        const sort = getParameterByName("sort") || "title-asc-rating-desc";

        if (genre != null) {
            state = {
                type: "browse",
                genre: genre,
                page: 1,
                sort: sort,
                itemsPerPage: 25
            }
        } else if (prefix != null) {
            state = {
                type: "browse",
                prefix: prefix,
                page: 1,
                sort: sort,
                itemsPerPage: 25
            }
        } else {
            // State for search
        }

        sessionStorage.setItem("movieListState", JSON.stringify(state));
    }

    const params = new URLSearchParams();
    Object.entries(state).forEach(([key, value]) => {
        if (key !== "type") {
            params.append(key, value);
        }
    });

    jQuery.ajax({
        dataType: "json",
        method: "GET",
        url: `api/${state["type"]}?${params.toString()}`,
        success: (resultData) => handleResult(resultData)
    });
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

showResults();

// $("#options-form").submit(submitOptionsForm);
$(document).on("submit", ".cart-form", submitCartForm);