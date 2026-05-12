package lk.ijse.seranity_mental_health_therapy_center.dao;

import java.util.List;

public interface CrudDAO<T> {

    boolean save(T t) throws Exception;

    boolean update(T t) throws Exception;

    boolean delete(String id) throws Exception;

    T search(String id) throws Exception;

    List<T> getAll() throws Exception;

    String getNextId() throws Exception;

    String generateNextId() throws Exception;
}