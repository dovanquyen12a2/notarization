package com.vinorsoft.microservices.core.notarization.util.repository_base.jdbc;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.vinorsoft.microservices.core.notarization.util.helpers.DataHelper;
import com.vinorsoft.microservices.core.notarization.util.helpers.KeyValuePair;

public interface IRepository<T> {

        /**
         * Thêm mới hoặc cập nhật vào table
         * Tạo procedure tên dạng TENBANG_ADD, ví dụ: USER_ADD, chú ý các tham số của
         * procedure phải có thứ tự giống hệt với thứ tự các trường trong lớp entity
         * Gọi hàm commit để hoàn thành cập nhật
         * 
         * @param entity
         * @return
         * @throws IllegalArgumentException
         * @throws SQLException
         */
        Boolean Add(T entity) throws SQLException, IllegalArgumentException;

        /**
         * Thêm mới hoặc cập nhật vào table
         * Tạo procedure tên dạng TENBANG_UPDATE, ví dụ: USER_UPDATE, chú ý các tham số
         * của procedure phải có thứ tự giống hệt với thứ tự các trường trong lớp entity
         * Gọi hàm commit để hoàn thành cập nhật
         * 
         * @param entity
         * @return
         * @throws IllegalArgumentException
         * @throws SQLException
         */
        Boolean Update(T entity) throws SQLException, IllegalArgumentException;

        /**
         * Xóa bản ghi trong table theo id
         * Tạo procedure tên dạng TENBANG_DELETE, ví dụ: USER_DELETE
         * Gọi hàm commit để hoàn thành cập nhật
         * 
         * @param id
         * @throws SQLException
         */
        void Delete(Long id) throws SQLException;

        /**
         * Xóa logic bản ghi trong table bằng cách thay đổi giá trị của trường status
         * Áp dụng đối với các yêu cầu không xóa vĩnh viễn dữ liệu
         * Tạo procedure tên dạng TENBANG_DELETEBY_STATUS, ví dụ: USER_DELETEBY_STATUS
         * Gọi hàm commit để hoàn thành cập nhật
         * 
         * @param id
         * @throws SQLException
         */
        void DeleteByStatus(Long id) throws SQLException;

        /**
         * Lấy bản ghi theo id
         * Tạo procedure tên dạng TENBANG_GETBYID, ví dụ: USER_GETBYID
         * 
         * @param id
         * @return
         * @throws SQLException
         * @throws IllegalArgumentException
         */
        T GetSingleById(Object id) throws SQLException, IllegalArgumentException;

        /**
         * Lấy một bản ghi đầu tiên thỏa mãn điều kiện trong bảng
         * Truyền vào list các tham số theo đúng thứ tự tham số trong procedure
         * Ví dụ lấy theo tên và tuổi như sau
         * Tạo procedure tên dạng TENBANG_GETSINGLEBY_TENTRUONG, ví dụ:
         * USER_GETSINGLEBY_NAMEAGE
         * 
         * @param param
         * @return
         * @throws SQLException
         */
        T GetSingleByCondition(List<KeyValuePair<String, Object>> param)
                        throws SQLException, IllegalArgumentException;

        /**
         * Lấy tất cả bản ghi trong table
         * Tạo procedure tên dạng TENBANG_GETALL, ví dụ: USER_GETALL
         * 
         * @return Collection<T>
         * @throws SQLException
         */
        Collection<T> GetAll() throws SQLException, IllegalArgumentException;

        /**
         * Lấy các bản ghi thỏa mãn điều kiện trong bảng
         * Truyền vào list các tham số theo đúng thứ tự tham số trong procedure
         * Ví dụ lấy theo tên và tuổi như sau
         * Tạo procedure tên dạng TENBANG_GETMULTIBY_TENTRUONG, ví dụ:
         * USER_GETSINGLEBY_NAMEAGE
         * 
         * @param param
         * @return
         * @throws SQLException
         * @throws IllegalArgumentException
         */
        Collection<T> GetMultiByCondition(List<KeyValuePair<String, Object>> param)
                        throws SQLException, IllegalArgumentException;

        /**
         * Lấy bản ghi theo phân trang
         * Tạo procedure tên dạng TENBANG_GETMULTIBY_PAGING, ví dụ:
         * USER_GETSINGLEBY_PAGING
         * 
         * @param search
         * @param page    start form 0
         * @param size    start from 1
         * @param orderby must be null or field name with exact case of character
         * @param sort
         * @return
         * @throws SQLException
         * @throws IllegalArgumentException
         */
        Collection<T> GetMultiPaging(String search, Integer page, Integer size, String orderby,
                        DataHelper.SortSqlQuery sort)
                        throws SQLException, IllegalArgumentException;

        /**
         * Check bản ghi theo điều kiện
         * Truyền vào list các tham số theo đúng thứ tự tham số trong procedure
         * Tạo procedure tên dạng TENBANG_CONTAIN_TENTRUONG, ví dụ:
         * USER_CONTAIN_NAMEAGE
         * 
         * @param param
         * @return
         * @throws SQLException
         * @throws IllegalArgumentException
         */
        Boolean CheckContains(List<KeyValuePair<String, Object>> param)
                        throws SQLException, IllegalArgumentException;

        /**
         * Đếm số bản ghi của table
         * Query trực tiếp không cần tạo procedure
         * 
         * @return
         * @throws SQLException
         * @throws IllegalArgumentException
         */
        Integer Count() throws SQLException, IllegalArgumentException;

        /**
         * Đếm số bản ghi theo điều kiện
         * Truyền vào list các tham số theo đúng thứ tự tham số trong procedure
         * Ví dụ lấy theo tên và tuổi như sau
         * Tạo procedure tên dạng TENBANG_COUNTBY_TENTRUONG, ví dụ:
         * USER_COUNTBY_NAMEAGE
         * 
         * @param param
         * @return
         * @throws SQLException
         * @throws IllegalArgumentException
         */
        Integer CountContain(List<KeyValuePair<String, Object>> param)
                        throws SQLException, IllegalArgumentException;
}
