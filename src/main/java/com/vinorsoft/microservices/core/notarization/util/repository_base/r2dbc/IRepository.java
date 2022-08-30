package com.vinorsoft.microservices.core.notarization.util.repository_base.r2dbc;

import java.sql.SQLException;
import java.util.List;

import com.vinorsoft.microservices.core.notarization.util.helpers.DataHelper;
import com.vinorsoft.microservices.core.notarization.util.helpers.KeyValuePair;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IRepository<T> {

        /**
         * Thêm mới hoặc cập nhật vào table
         * Tạo procedure tên dạng TENBANG_ADD, ví dụ: USER_ADD, chú ý các tham số của
         * procedure phải có thứ tự giống hệt với thứ tự các trường trong lớp entity
         * Gọi hàm commit để hoàn thành cập nhật
         * 
         * @param entity
         * @return
         * @throws SQLException
         */
        Mono<Boolean> Add(T entity) throws SQLException;

        /**
         * Thêm mới hoặc cập nhật vào table
         * Tạo procedure tên dạng TENBANG_UPDATE, ví dụ: USER_UPDATE, chú ý các tham số
         * của procedure phải có thứ tự giống hệt với thứ tự các trường trong lớp entity
         * Gọi hàm commit để hoàn thành cập nhật
         * 
         * @param entity
         * @return
         * @throws SQLException
         */
        Mono<Boolean> Update(T entity) throws SQLException;

        /**
         * Xóa bản ghi trong table theo id
         * Tạo procedure tên dạng TENBANG_DELETE, ví dụ: USER_DELETE
         * Gọi hàm commit để hoàn thành cập nhật
         * 
         * @param id
         * @throws SQLException
         */
        Mono<Void> Delete(Long id) throws SQLException;

        /**
         * Xóa tất cả bản ghi
         * 
         * @return
         * @throws SQLException
         */
        Mono<Void> DeleteAll() throws SQLException;

        /**
         * Xóa logic bản ghi trong table bằng cách thay đổi giá trị của trường status
         * Áp dụng đối với các yêu cầu không xóa vĩnh viễn dữ liệu
         * Tạo procedure tên dạng TENBANG_DELETEBY_STATUS, ví dụ: USER_DELETEBY_STATUS
         * Gọi hàm commit để hoàn thành cập nhật
         * 
         * @param id
         * @throws SQLException
         */
        Mono<Void> DeleteByStatus(Long id) throws SQLException;

        /**
         * Lấy bản ghi theo id
         * Tạo procedure tên dạng TENBANG_GETBYID, ví dụ: USER_GETBYID
         * 
         * @param id
         * @return
         * @throws SQLException
         * 
         */
        Mono<T> GetSingleById(Object id) throws SQLException;

        /**
         * Lấy một bản ghi đầu tiên thỏa mãn điều kiện trong bảng
         * Truyền vào list các tham số theo đúng thứ tự tham số trong procedure
         * Ví dụ lấy theo tên và tuổi như sau
         * Tạo procedure tên dạng TENBANG_GETSINGLEBY_TENTRUONG, ví dụ:
         * USER_GETSINGLEBY_NAMEAGE
         * 
         * @param params
         * @return
         * @throws SQLException
         */
        Mono<T> GetSingleByCondition(List<KeyValuePair<String, Object>> params)
                        throws SQLException;

        /**
         * Lấy tất cả bản ghi trong table
         * Tạo procedure tên dạng TENBANG_GETALL, ví dụ: USER_GETALL
         * 
         * @return Collection<T>
         * @throws SQLException
         */
        Flux<T> GetAll() throws SQLException;

        /**
         * Lấy các bản ghi thỏa mãn điều kiện trong bảng
         * Truyền vào list các tham số theo đúng thứ tự tham số trong procedure
         * Ví dụ lấy theo tên và tuổi như sau
         * Tạo procedure tên dạng TENBANG_GETMULTIBY_TENTRUONG, ví dụ:
         * USER_GETSINGLEBY_NAMEAGE
         * 
         * @param params
         * @return
         * @throws SQLException
         * 
         */
        Flux<T> GetMultiByCondition(List<KeyValuePair<String, Object>> params)
                        throws SQLException;

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
         * 
         */
        Flux<T> GetMultiPaging(String search, Integer page, Integer size, String orderby,
                        DataHelper.SortSqlQuery sort)
                        throws SQLException;

        /**
         * Check bản ghi theo điều kiện
         * Truyền vào list các tham số theo đúng thứ tự tham số trong procedure
         * Tạo procedure tên dạng TENBANG_CONTAIN_TENTRUONG, ví dụ:
         * USER_CONTAIN_NAMEAGE
         * 
         * @param params
         * @return
         * @throws SQLException
         * 
         */
        Mono<Boolean> CheckContains(List<KeyValuePair<String, Object>> params)
                        throws SQLException;

        /**
         * Đếm số bản ghi của table
         * Query trực tiếp không cần tạo procedure
         * 
         * @return
         * @throws SQLException
         * 
         */
        Mono<Integer> Count() throws SQLException;

        /**
         * Đếm số bản ghi theo điều kiện
         * Truyền vào list các tham số theo đúng thứ tự tham số trong procedure
         * Ví dụ lấy theo tên và tuổi như sau
         * Tạo procedure tên dạng TENBANG_COUNTBY_TENTRUONG, ví dụ:
         * USER_COUNTBY_NAMEAGE
         * 
         * @param params
         * @return
         * @throws SQLException
         * 
         */
        Mono<Integer> CountContain(List<KeyValuePair<String, Object>> params)
                        throws SQLException;
}
