package com.vinorsoft.microservices.core.notarization.util.repository.jdbc;

import static org.junit.Assert.assertTrue;

import java.sql.SQLException;

import java.util.Collection;
import java.util.Date;
import java.util.Random;

import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import com.vinorsoft.microservices.core.notarization.util.repository.Notarization;
import com.vinorsoft.microservices.core.notarization.util.repository.NotarizationInitQuery;

@Import(ResponsitoryTestBase.class)
@RunWith(SpringRunner.class)
@SpringBootTest
public class ResponsitoryBaseQueryTests extends ResponsitoryTestBase {

    Random random = new Random();
    @Override
    public String toString() {
        return """
                ResponsitoryBaseTests:
                    Scritp mẫu trong file TestTableInitQuery.java
                    1 Chạy script tạo table test
                    2 Chạy script tạo các stored procedure
                    3 Chạy test case
                    """;
    }
    void setupDb() throws SQLException {
        Notarization item = new Notarization();
        item.setId(12345678L);

        item.CODE = "MP31233123";
        item.CUSTOMER_CODE = "123456789";
        item.CUSTOMER_NAME = "custormer";
        item.CUSTOMER_PHONE = "0928299290";
        item.CUSTOMER_EMAIL = "email.customer@gmail.com";
        item.RM_NAME = "RM";
        item.RM_PHONE = "0928299290";
        item.OPTION_CODE = "123456789";
        item.OPTION_T24_CODE = "123456789";
        item.BRANCH_CODE = "123456789";
        item.BRANCH_NAME = "branch";
        item.CURRENT_USER_ID = 123456L;
        item.PARENT_ID = 123456L;
        item.FINISH_PRINTING_TIME = new Date();
        item.LOCATION_NOTARIZATION = "Ha Noi";
        item.TIME_NOTARIZATION = new Date();
        item.STATUS = random.nextInt();
        respository.Delete(item.getId());
        unitOfWork.commit();
        respository.Add(item);
        unitOfWork.commit();
        Notarization result = respository.GetSingleById(item.getId());
        assertTrue("Result muse be null", result != null);
    }

    @Test
    public void AddCommitTest() throws IllegalArgumentException, SQLException {

        Notarization newItem = new Notarization();
        newItem.setId(123456789L);
        newItem.CODE = "MP3123312345";                                                                          // = 20
        newItem.CUSTOMER_CODE = "123456789";
        newItem.CUSTOMER_NAME = "custormer";
        newItem.CUSTOMER_PHONE = "0928299290";
        newItem.CUSTOMER_EMAIL = "email.customer@gmail.com";
        newItem.RM_NAME = "RM";
        newItem.RM_PHONE = "0928299290";
        newItem.OPTION_CODE = "123456789";
        newItem.OPTION_T24_CODE = "123456789";
        newItem.BRANCH_CODE = "123456789";
        newItem.BRANCH_NAME = "branch";
        newItem.CURRENT_USER_ID = 123456L;
        newItem.PARENT_ID = 123456L;
        newItem.FINISH_PRINTING_TIME = new Date();
        newItem.LOCATION_NOTARIZATION = "Ha Noi";
        newItem.TIME_NOTARIZATION = new Date();
        newItem.STATUS = random.nextInt();
        respository.Delete(newItem.getId());
        unitOfWork.commit();
        respository.Add(newItem);
        unitOfWork.commit();
        Notarization result = respository.GetSingleById(newItem.getId());
        assertTrue("Result muse be null", result != null);
    }

    @Test
    public void UpdateCommitTest() throws IllegalArgumentException, SQLException {
        setupDb();
        Notarization result = respository.GetSingleById(12345678L);
        result.STATUS = random.nextInt();

        respository.Update(result);
        unitOfWork.commit();
        Notarization newItem = respository.GetSingleById(12345678L);

        assertTrue("New item must have string field as same as item1 string field",
                newItem.STATUS.equals(result.STATUS));
    }

    @Test
    public void GetAllTest() throws SQLException, IllegalArgumentException {
        setupDb();
        Collection<Notarization> results = respository.GetAll();
        assertTrue("Must have recourd", results.toArray().length > 0);
    }

    @Test

    public void GetSingerByIdTest() throws SQLException {
        setupDb();
        Notarization result = respository.GetSingleById(12345678L);
        assertTrue("Must have record", result != null);
    }

    @Test
    public void GetSingerByIdNoRecordTest() throws SQLException {
        setupDb();
        Notarization result = respository.GetSingleById(new Random().nextLong());
        assertTrue("Result must be null", result == null);
    }

    @Test
    @Order(7)
    public void CountAllTest() throws SQLException {
        setupDb();
        Integer count = respository.Count();
        assertTrue("Result must be greater than 1", count > 0);
    }

    @Test
    @Order(8)
    public void DeleteTest() throws SQLException, IllegalArgumentException {
        setupDb();
        respository.Delete(12345678L);
        unitOfWork.commit();

        Notarization result = respository.GetSingleById(12345678L);
        assertTrue("Result muse be null", result == null);
    }
}
