package vn.iotstar.controller;

import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.entity.Category;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.repository.CategoryRepository;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Controller
@Transactional
public class GraphQLController {

    private final ProductRepository productRepo;
    private final UserRepository userRepo;
    private final CategoryRepository categoryRepo;

    public GraphQLController(ProductRepository productRepo,
                             UserRepository userRepo,
                             CategoryRepository categoryRepo) {
        this.productRepo = productRepo;
        this.userRepo = userRepo;
        this.categoryRepo = categoryRepo;
    }

    // ---------- Queries ----------
    @QueryMapping
    public List<Product> productsSortedByPrice() {
        return productRepo.findAllByOrderByPriceAsc();
    }

    @QueryMapping
    public List<Product> productsByCategory(@Argument Long categoryId) {
        return productRepo.findByCategoryId(categoryId);
    }

    @QueryMapping
    public List<User> users() { return userRepo.findAll(); }

    @QueryMapping
    public User user(@Argument Long id) { return userRepo.findById(id).orElse(null); }

    @QueryMapping
    public List<Category> categories() { return categoryRepo.findAll(); }

    @QueryMapping
    public Category category(@Argument Long id) { return categoryRepo.findById(id).orElse(null); }

    @QueryMapping
    public Product product(@Argument Long id) { return productRepo.findById(id).orElse(null); }

    // ---------- Mutations ----------
    @MutationMapping
    public User createUser(@Argument CreateUserInput input) {
        User u = new User();
        u.setFullname(input.getFullname());
        u.setEmail(input.getEmail());
        u.setPassword(input.getPassword());
        u.setPhone(input.getPhone());
        return userRepo.save(u);
    }

    @MutationMapping
    public User updateUser(@Argument Long id, @Argument UpdateUserInput input) {
        return userRepo.findById(id).map(u -> {
            if (input.getFullname() != null) u.setFullname(input.getFullname());
            if (input.getEmail() != null) u.setEmail(input.getEmail());
            if (input.getPassword() != null) u.setPassword(input.getPassword());
            if (input.getPhone() != null) u.setPhone(input.getPhone());
            return userRepo.save(u);
        }).orElse(null);
    }

    @MutationMapping
    public Boolean deleteUser(@Argument Long id) {
        return userRepo.findById(id).map(u -> {
            userRepo.delete(u);
            return true;
        }).orElse(false);
    }

    @MutationMapping
    public Category createCategory(@Argument CreateCategoryInput input) {
        Category c = new Category();
        c.setName(input.getName());
        c.setImages(input.getImages());
        return categoryRepo.save(c);
    }

    @MutationMapping
    public Category updateCategory(@Argument Long id, @Argument UpdateCategoryInput input) {
        return categoryRepo.findById(id).map(c -> {
            if (input.getName() != null) c.setName(input.getName());
            if (input.getImages() != null) c.setImages(input.getImages());
            return categoryRepo.save(c);
        }).orElse(null);
    }

    @MutationMapping
    public Boolean deleteCategory(@Argument Long id) {
        return categoryRepo.findById(id).map(c -> {
            categoryRepo.delete(c);
            return true;
        }).orElse(false);
    }

    @MutationMapping
    public Product createProduct(@Argument CreateProductInput input) {
        Product p = new Product();
        p.setTitle(input.getTitle());
        p.setQuantity(input.getQuantity());
        p.setDesc(input.getDesc());
        p.setPrice(input.getPrice());
        User owner = userRepo.findById(input.getUserid()).orElse(null);
        p.setUser(owner);
        Category cat = categoryRepo.findById(input.getCategoryId()).orElse(null);
        p.setCategory(cat);
        return productRepo.save(p);
    }

    @MutationMapping
    public Product updateProduct(@Argument Long id, @Argument UpdateProductInput input) {
        return productRepo.findById(id).map(p -> {
            if (input.getTitle() != null) p.setTitle(input.getTitle());
            if (input.getQuantity() != null) p.setQuantity(input.getQuantity());
            if (input.getDesc() != null) p.setDesc(input.getDesc());
            if (input.getPrice() != null) p.setPrice(input.getPrice());
            if (input.getUserid() != null) {
                userRepo.findById(input.getUserid()).ifPresent(p::setUser);
            }
            if (input.getCategoryId() != null) {
                categoryRepo.findById(input.getCategoryId()).ifPresent(p::setCategory);
            }
            return productRepo.save(p);
        }).orElse(null);
    }

    @MutationMapping
    public Boolean deleteProduct(@Argument Long id) {
        return productRepo.findById(id).map(p -> {
            productRepo.delete(p);
            return true;
        }).orElse(false);
    }
}