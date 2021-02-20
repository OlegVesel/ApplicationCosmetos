package com.home.ApplicationCosmetos.Model;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.*;
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
    @NotNull
    private String volume;
    private int time_after_opening; //срок после вскрытия
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate shelf_life; //срок годности
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate autopsy_date; // дата вскрытия
    @Size(max = 2048)
    private String note; //примечания
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date_death; //когда испортится

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userid")
    private User owner;

    public CosmeticProduct() {
    }

    public CosmeticProduct(String name, String brand, String volume, int time_after_opening,
                           LocalDate shelf_life, LocalDate autopsy_date, String note, LocalDate date_death, User owner) {

        this.name = name;
        this.brand = brand;
        this.volume = volume;
        this.time_after_opening = time_after_opening;
        this.shelf_life = shelf_life;
        this.autopsy_date = autopsy_date;
        this.note = note;
        this.date_death = date_death;
        this.owner = owner;
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

    public LocalDate getShelf_life() {
        return shelf_life;
    }

    public void setShelf_life(LocalDate shelf_life) {
        this.shelf_life = shelf_life;
    }

    public LocalDate getAutopsy_date() {
        return autopsy_date;
    }

    public void setAutopsy_date(LocalDate autopsy_date) {
        this.autopsy_date = autopsy_date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getDate_death() {
        return date_death;
    }

    public void setDate_death(LocalDate date_death) {
        this.date_death = date_death;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "CosmeticProduct{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", brand='" + brand + '\'' +
                ", volume='" + volume + '\'' +
                ", time_after_opening=" + time_after_opening +
                ", shelf_life=" + shelf_life +
                ", autopsy_date=" + autopsy_date +
                ", note='" + note + '\'' +
                ", date_death=" + date_death + '\'' +
                '}';
    }
}
