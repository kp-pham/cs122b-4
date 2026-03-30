function handleResult(resultData) {
    let itemsList = $("#items-table-body");
    let rows = "";

    resultData["items"].forEach(item => {
        rows += `
            <tr id="${item['id']}">
                <td>
                    <a href="single-movie.html?id=${item['id']}">${item['title']}</a>
                </td>
                <td class="d-flex gap-2">
                    <form class="subtract-form" method="POST" action="#">
                        <input type="hidden" name="id" value="${item['id']}">
                        <button type="submit" class="rounded text-white bg-dark">-</button>
                    </form>
                    ${item['quantity']}
                    <form class="add-form" method="POST" action="#">
                        <input type="hidden" name="id" value="${item['id']}">
                        <button type="submit" class="rounded text-white bg-dark">+</button>
                    </form>
                </td>
                <td>
                    <form id="removeForm" method="POST" action="#">
                        <button class="rounded text-white" style="background-color: #fe0000; border-color: #fe0000;">Remove</button>
                    </form>
                </td>
                <td>$${item['price']}</td>
                <td>$${item['subtotal']}</td>
            </tr>
        `;
    });

    itemsList.html(rows);

    let itemsTable = $("#items-table");

    let footer = `
        <tfoot id="items-table-footer">
            <tr>
                <td colspan="3"></td>
                <td class="text-end text-dark fw-bold">Total:</td>
                <td>$${resultData["total"]}</td>
            </tr>
        </tfoot>
    `;

    $("#items-table-footer").remove();
    itemsTable.append(footer);
}

function submitAddForm(submitFormEvent) {
    submitFormEvent.preventDefault();

    let id = $(this).find("input[name='id']").val();

    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: `api/cart?action=add&id=${encodeURIComponent(id)}`,
        success: (resultData) => handleResult(resultData)
    });
}

function submitSubtractForm(submitFormEvent) {
    submitFormEvent.preventDefault();

    let id = $(this).find("input[name='id']").val();

    jQuery.ajax({
        dataType: "json",
        method: "POST",
        url: `api/cart?action=subtract&id=${encodeURIComponent(id)}`,
        success: (resultData) => handleResult(resultData)
    });
}

jQuery.ajax({
    dataType: "json",
    method: "GET",
    url: "api/cart",
    success: (resultData) => handleResult(resultData)
});

$(document).on("submit", ".add-form", submitAddForm);
$(document).on("submit", ".subtract-form", submitSubtractForm);