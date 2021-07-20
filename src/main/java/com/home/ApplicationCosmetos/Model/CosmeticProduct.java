package com.home.ApplicationCosmetos.Model;

import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

@Entity
public class CosmeticProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank(message = "Поле \"Название\" не должно быть пустым")
    @Length(max = 255, message = "Поле \"Название\" не должно превышать 255 символов")
    private String name;
    @NotBlank(message = "Поле \"Бренд\" не должно быть пустым")
    @Length(max = 255, message = "Поле \"Бренд\" не должно превышать 255 символов")
    private String brand;
    private String volume;
    @NotNull(message = "Укажите срок после вскрытия, если его нет поставьте 0")
    private int time_after_opening; //срок после вскрытия
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "Укажите срок годности")
    private LocalDate shelfLife; //срок годности
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate autopsyDate; // дата вскрытия
    @Size(max = 2048, message = "Примечание не должно быть больше чем 2048 символов")
    private String note; //примечания
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateDeath; //когда испортится

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid")
    private User owner;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateCreate; //дата добавления продукта



    public CosmeticProduct() {
    }

    public CosmeticProduct(String name, String brand, String volume, int time_after_opening,
                           LocalDate shelfLife, LocalDate autopsyDate, String note, LocalDate dateDeath, User owner, LocalDate dateCreate) {

        this.name = name;
        this.brand = brand;
        this.volume = volume;
        this.time_after_opening = time_after_opening;
        this.shelfLife = shelfLife;
        this.autopsyDate = autopsyDate;
        this.note = note;
        this.dateDeath = dateDeath;
        this.owner = owner;
        this.dateCreate = dateCreate;
    }


    public String getOwnerName() {
        return owner != null ? owner.getUsername() : "<none>";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public int getTime_after_opening() {
        return time_after_opening;
    }

    public void setTime_after_opening(int time_after_opening) {
        this.time_after_opening = time_after_opening;
    }

    public LocalDate getShelfLife() {
        return shelfLife;
    }

    public void setShelfLife(LocalDate shelfLife) {
        this.shelfLife = shelfLife;
    }

    public LocalDate getAutopsyDate() {
        return autopsyDate;
    }

    public void setAutopsyDate(LocalDate autopsyDate) {
        this.autopsyDate = autopsyDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getDateDeath() {
        return dateDeath;
    }

    public void setDateDeath(LocalDate dateDeath) {
        this.dateDeath = dateDeath;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public LocalDate getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(LocalDate dateCreate) {
        this.dateCreate = dateCreate;
    }
    @Override
    public String toString() {
        return "CosmeticProduct{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", volume='" + volume + '\'' +
                ", time_after_opening=" + time_after_opening +
                ", shelf_life=" + shelfLife +
                ", autopsy_date=" + autopsyDate +
                ", note='" + note + '\'' +
                ", date_death=" + dateDeath + '\'' +
                '}';
    }
}
