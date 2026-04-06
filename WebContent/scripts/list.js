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
    const sort = getParameterByName("sort") || "title-asc-rating-desc";
    const page = getParameterByName("page") || 1;
    const offset = getParameterByName("offset") || 1;

    const sortDropdown = $("#sort");
    sortDropdown.val(sort);

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

function hasSearchParams() {
    return window.location.search.length > 0;
}

function isValid(state) {
    return state && (state.type === "browse" || state.type === "search");
}

function showResults() {
    let state = null;

    let storedState = JSON.parse(sessionStorage.getItem("movieListState"));

    if (hasSearchParams()) {
        const genre = getParameterByName("genre");
        const prefix = getParameterByName("prefix");
        const title = getParameterByName("title");
        const year = getParameterByName("year");
        const director = getParameterByName("director");
        const star = getParameterByName("star");
        const sort = getParameterByName("sort") || "title-asc-rating-desc";
        const page = getParameterByName("page") || 1;
        const offset = getParameterByName("offset") || 25;

        if (genre != null) {
            state = {
                type: "browse",
                genre: genre,
                sort: sort,
                page: page,
                offset: offset
            }
        } else if (prefix != null) {
            state = {
                type: "browse",
                prefix: prefix,
                sort: sort,
                page: page,
                offset: offset
            }
        } else {
            state = {
                type: "search",
                title: title,
                year: year,
                director: director,
                star: star,
                sort: sort,
                page: page,
                offset: offset
            }
        }

        sessionStorage.setItem("movieListState", JSON.stringify(state));

    } else if (isValid(storedState)) {
        state = storedState;

    } else {
        return;
    }
    const params = new URLSearchParams();
    Object.entries(state).forEach(([key, value]) => {
        if (key !== "type" && value != null) {
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

function submitOptionsForm(formSubmitEvent) {
    formSubmitEvent.preventDefault();

    const params = new URLSearchParams(window.location.search);
    const sort = $("select[name=sort]").val();
    params.set("sort", sort);

    window.location.href = `list.html?${params.toString()}`;
}

showResults();

$("#options-form").submit(submitOptionsForm);
$(document).on("submit", ".cart-form", submitCartForm);