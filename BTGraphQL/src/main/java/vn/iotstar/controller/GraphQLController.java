package vn.iotstar.controller;

import org.springframework.graphql.data.method.annotation.*;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;

import vn.iotstar.dto.CreateCategoryInput;
import vn.iotstar.dto.CreateProductInput;
import vn.iotstar.dto.CreateUserInput;
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

	   private final UserRepository userRepo;
	    private final ProductRepository productRepo;
	    private final CategoryRepository categoryRepo;

	    public GraphQLController(UserRepository userRepo,
	                             ProductRepository productRepo,
	                             CategoryRepository categoryRepo) {
	        this.userRepo = userRepo;
	        this.productRepo = productRepo;
	        this.categoryRepo = categoryRepo;
	    }

	    // Queries
	    @QueryMapping
	    public List<Product> productsSortedByPriceAsc() {
	        return productRepo.findAllByOrderByPriceAsc();
	    }

	    @QueryMapping
	    public List<Product> productsByCategory(@Argument Long categoryId) {
	        return productRepo.findByCategoryId(categoryId);
	    }

	    @QueryMapping
	    public List<User> users() { return userRepo.findAll(); }

	    @QueryMapping
	    public List<Category> categories() { return categoryRepo.findAll(); }

	    // Mutations
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
	    public Category createCategory(@Argument CreateCategoryInput input) {
	        Category c = new Category();
	        c.setName(input.getName());
	        c.setImages(input.getImages());
	        return categoryRepo.save(c);
	    }

	    @MutationMapping
	    public Product createProduct(@Argument CreateProductInput input) {
	        Product p = new Product();
	        p.setTitle(input.getTitle());
	        p.setQuantity(input.getQuantity());
	        p.setDesc(input.getDesc());
	        p.setPrice(input.getPrice());

	        // Đã khắc phục lỗi Null ID
	        Long userId = input.getUserId();
	        if (userId != null) {
	            userRepo.findById(userId).ifPresent(p::setUser);
	        }
	        
	        // Đã khắc phục lỗi Null ID và thêm Category an toàn
	        if (input.getCategoryIds() != null) {
	            input.getCategoryIds().stream()
	                .filter(cid -> cid != null) 
	                .forEach(cid -> 
	                    categoryRepo.findById(cid).ifPresent(p.getCategories()::add)
	                );
	        }
	        
	        return productRepo.save(p); // <--- Lỗi CME xảy ra xung quanh đây
	    }
	    
	    @MutationMapping
	    @Transactional
	    public User updateUser(@Argument Long id, @Argument CreateUserInput input) {
	        return userRepo.findById(id).map(u -> {
	            u.setFullname(input.getFullname());
	            u.setEmail(input.getEmail());
	            u.setPassword(input.getPassword());
	            u.setPhone(input.getPhone());
	            return userRepo.save(u);
	        }).orElseThrow(() -> new RuntimeException("User not found"));
	    }

	    @MutationMapping
	    @Transactional
	    public Boolean deleteUser(@Argument Long id) {
	        if (userRepo.existsById(id)) {
	            userRepo.deleteById(id);
	            return true;
	        }
	        return false;
	    }

	    // Category update/delete
	    @MutationMapping
	    @Transactional
	    public Category updateCategory(@Argument Long id, @Argument CreateCategoryInput input) {
	        return categoryRepo.findById(id).map(c -> {
	            c.setName(input.getName());
	            c.setImages(input.getImages());
	            return categoryRepo.save(c);
	        }).orElseThrow(() -> new RuntimeException("Category not found"));
	    }

	    @MutationMapping
	    @Transactional
	    public Boolean deleteCategory(@Argument Long id) {
	        if (categoryRepo.existsById(id)) {
	            categoryRepo.deleteById(id);
	            return true;
	        }
	        return false;
	    }

	    // Product update/delete
	    @MutationMapping
	    @Transactional
	    public Product updateProduct(@Argument Long id, @Argument CreateProductInput input) {
	        return productRepo.findById(id).map(p -> {
	            p.setTitle(input.getTitle());
	            p.setQuantity(input.getQuantity());
	            p.setDesc(input.getDesc());
	            p.setPrice(input.getPrice());
	            userRepo.findById(input.getUserId()).ifPresent(p::setUser);

	            p.getCategories().clear();
	            for (Long catId : input.getCategoryIds()) {
	                categoryRepo.findById(catId).ifPresent(p.getCategories()::add);
	            }
	            return productRepo.save(p);
	        }).orElseThrow(() -> new RuntimeException("Product not found"));
	    }

	    @MutationMapping
	    @Transactional
	    public Boolean deleteProduct(@Argument Long id) {
	        if (productRepo.existsById(id)) {
	            productRepo.deleteById(id);
	            return true;
	        }
	        return false;
	    }
}