package org.csci4050.bookstore.Bookstore.service;

import org.csci4050.bookstore.Bookstore.dao.BookDao;
import org.csci4050.bookstore.Bookstore.exceptions.ValidationException;
import org.csci4050.bookstore.Bookstore.model.Book;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class BookService {

    private BookDao bookDao;

    private PromotionService promotionService;

    private VendorService vendorService;

    @Autowired
    public BookService(final BookDao bookDao, final PromotionService promotionService, final VendorService vendorService) {
        this.bookDao = bookDao;
        this.promotionService = promotionService;
        this.vendorService = vendorService;
    }

    public Optional<Book> getBook(final String isbn) {
        return bookDao.getBook(isbn);
    }

    public void insertBook(final Book book) throws ValidationException {
        if (book.getPromoId() != null) {
            this.checkPromotionExists(book.getPromoId());
        }
        this.checkVendorUsernameExists(book.getVUsername());
        bookDao.createBook(book);
    }

    public void updateBook(final Book book) throws ValidationException {
        if (!bookDao.getBook(book.getIsbn()).isPresent()) {
            throw new ValidationException("Book with isbn <%s> does not exist", book.getIsbn());
        }
        bookDao.updateBook(book);
    }

    public List<Book> getBooks() {
        return this.bookDao.getBooks();
    }

    public List<Book> queryBooks(final String column, final String value) throws ValidationException {
        final List<String> validColumns = Arrays.asList("isbn", "author", "title");
        if (!validColumns.contains(column)) {
            throw new ValidationException("Value <%s> is not a valid query parameter", column);
        }
        return bookDao.queryBooks(column, value);
    }

    public List<Book> getBooksByColumns(final String column, final String value) {
        return bookDao.queryBooks(column, value);
    }

    public List<String> getCategoryValues() {
        return bookDao.getValuesByColumn("category");
    }

    private void checkPromotionExists(final int promoId) throws ValidationException {
        if (!promotionService.getPromotion(promoId).isPresent()) {
            throw new ValidationException("Promo id <%s> does not exist", Integer.toString(promoId));
        }
    }

    private void checkVendorUsernameExists(final String vUsername) throws ValidationException {
        if (!vendorService.getVendor(vUsername).isPresent()) {
            throw new ValidationException("Vendor with username <%s> does not exist", vUsername);
        }
    }
}
