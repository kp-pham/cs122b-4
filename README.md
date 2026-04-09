# cs122b-2
> Demonstration: [https://youtu.be/53tnRAXzK5g](https://youtu.be/53tnRAXzK5g)

The second project extends the first project and implements the login page, searching and browsing with substring matching, sorting search results and pagination, and session-based shopping carts.

## Features

* Login Page
  * HTTP POST
  * Unauthenticated users redirected from protected resources to login page
* Main Page
  * Search movies based on title, year, director, and star name
  * ``SearchServlet`` uses substring matching for title, director, and star name
    * ``AND M.title LIKE %<title>%``
    * ``AND M.director LIKE %<director>%``
    * ``AND S.name LIKE %<name>%``
  * Browse movies based on genre and prefix
  * ``BrowseServlet`` uses substring matching for prefixes
    * ``WHERE M.title LIKE <prefix>%``
* Movie List Page
  * First three genres sorted by alphabetical order
  * First three stars sorted by movie count in descending order and alphabetical order used to break ties
  * Sort list by either title or rating first and both in either ascending or descending order
  * Previous and next buttons to go between pages 
  * Choose number of movies on each page from list of predefined values
* Single Movie Page
  * All genres sorted by alphabetical order
  * All stars sorted by movie count in descending order and alphabetical order used to break ties
* Single Star Page
  * All movies sorted by year in descending order and alphabetical order used to break ties
* Jump Functionality
  * Status of Movie List Page maintained when returned from single pages
  * Store search/browse condition, sorting, and pagination setup in session instead of browser history
* Shopping Cart
  * Increase and decrease quantity of movies
  * Remove movies from cart
* Payment Page
  * Display total price of shopping cart
  * Ask customers for first and last name of credit card holder, credit card number, and expiration date
* Place Order Action
  * HTTP POST
  * Transaction succeeds and inserts sales records into the system
  * Transaction fails and shows error message for customers to re-enter payment information
