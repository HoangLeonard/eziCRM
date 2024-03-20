package com.ezicrm.eziCRM.service;

import com.ezicrm.eziCRM.model.CusSearchReqDTO;
import com.ezicrm.eziCRM.model.CustomerEntity;
import com.ezicrm.eziCRM.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService implements CRUDService<CustomerEntity> {

    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<CustomerEntity> getByID(Long id) throws IllegalArgumentException {
        return repository.findById(id);
    }

    @Override
    public List<CustomerEntity> getAll() {
        return repository.findAll();
    }

    @Override
    public Optional<CustomerEntity> update(CustomerEntity newCustomer) {
        long id = newCustomer.getCusId();

        Optional<CustomerEntity> foundCustomer = repository.findById(id);

        if (foundCustomer.isPresent() && id != 0) {
            CustomerEntity oldCustomer = foundCustomer.get();

            String newPhone = newCustomer.getPhone();
            String newEmail = newCustomer.getEmail();
            String newFacebook = newCustomer.getFacebook();

            if ((newPhone == null || !newPhone.equals(oldCustomer.getPhone()))
                || (newEmail == null || !newEmail.equals(oldCustomer.getEmail()))
                || (newFacebook == null || !newFacebook.equals(oldCustomer.getFacebook()))) {
                checkUpdateCondition(newCustomer);
            }
            return Optional.of(repository.save(newCustomer));
        } else {
            return insert(newCustomer);
        }
    }

    private void checkUpdateCondition(CustomerEntity entity) {
        String phone = entity.getPhone();
        String email = entity.getEmail();
        String facebook = entity.getFacebook();

        // Kiểm tra xem có ít nhất một phương thức liên lạc không null
        if (phone == null && email == null && facebook == null) {
            throw new IllegalArgumentException("Invalid customer, customer must have at least one contact method.");
        }

        // Kiểm tra xem có bản ghi nào đã tồn tại với các thông tin liên lạc của khách hàng mới
        List<CustomerEntity> foundEntities = repository.findByPhoneOrFacebookOrEmail(phone, facebook, email);
        if (foundEntities.size() > 1) {
            StringBuilder errorMessage = new StringBuilder("Invalid customer's contact methods:");
            int p = 0, f = 0, m = 0;
            for (CustomerEntity customer: foundEntities)
                if (customer.getCusId() != entity.getCusId()) {
                    if (customer.getEmail().equals(entity.getEmail())) m++;
                    if (customer.getPhone().equals(entity.getPhone())) p++;
                    if (customer.getFacebook().equals(entity.getFacebook())) f++;
                }

            if (p > 0) errorMessage.append(" - duplicated phone number");
            if (m > 0) errorMessage.append(" - duplicated email");
            if (f > 0) errorMessage.append(" - duplicated facebook url");

            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    @Override
    public Optional<CustomerEntity> insert(CustomerEntity entity) {
        checkInsertCondition(entity);
        return Optional.of(repository.save(entity));
    }

    private void checkInsertCondition(CustomerEntity entity) {
        String phone = entity.getPhone();
        String email = entity.getEmail();
        String facebook = entity.getFacebook();

        // Kiểm tra xem có ít nhất một phương thức liên lạc không null
        if (phone == null && email == null && facebook == null) {
            throw new IllegalArgumentException("Invalid customer, customer must have at least one contact method.");
        }

        // Kiểm tra xem có bản ghi nào đã tồn tại với các thông tin liên lạc của khách hàng mới
        List<CustomerEntity> foundEntities = repository.findByPhoneOrFacebookOrEmail(phone, facebook, email);
        if (!foundEntities.isEmpty()) {
            StringBuilder errorMessage = new StringBuilder("Invalid customer's contact methods. Found customers having the same contact methods: ---");
            foundEntities.forEach(customer -> errorMessage.append(customer).append(" --- "));
            throw new IllegalArgumentException(errorMessage.toString());
        }
    }

    @Override
    public boolean delete(Long id) throws IllegalArgumentException {
        Optional<CustomerEntity> c = getByID(id);
        if (c.isPresent()) {
            repository.deleteById(id);
            return true;
        } else return false;
    }

    public List<CustomerEntity> getCustomerByProperty(CusSearchReqDTO searchReqDTO) {
        if (searchReqDTO == null) searchReqDTO = new CusSearchReqDTO();
        return repository.findByProperty(searchReqDTO.getName(),
                searchReqDTO.getAgeRange()[0],
                searchReqDTO.getAgeRange()[1],
                searchReqDTO.getAddress(),
                searchReqDTO.getPhone(),
                searchReqDTO.getEmail(),
                searchReqDTO.getFacebook());
    }

    public List<CustomerEntity> getAllByID(List<Long> ids) {
        return repository.findAllById(ids);
    }

    public List<CustomerEntity> findByCategoryIds(List<Long> categoryIds){
        List<CustomerEntity> customers = repository.findByCategoryIds(categoryIds);
        if(customers.isEmpty()){
            return null;
        }
        return customers;
    }
}