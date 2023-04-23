package project.management.usersmanagement.entities;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "coupons")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    private BigDecimal Discount;
    private Calendar expirationDate;

    public BigDecimal getDiscount() {
        return Discount;
    }
    public boolean isValid() {
        Date currentDate = new Date();
        return this.expirationDate.after(currentDate);
    }
    @ManyToMany(mappedBy = "coupons")
    private List<User> users;
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
    public void setCode(String code) {
        this.code = code;
    }

    public void setDiscount(BigDecimal discount) {
        Discount = discount;
    }

    public void setExpirationDate(Calendar expirationDate) {
        this.expirationDate = expirationDate;
    }


}
