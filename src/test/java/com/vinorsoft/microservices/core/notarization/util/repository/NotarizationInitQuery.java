package com.vinorsoft.microservices.core.notarization.util.repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import com.thedeanda.lorem.Lorem;
import com.thedeanda.lorem.LoremIpsum;
import com.vinorsoft.microservices.core.notarization.util.helpers.DataHelper;
import com.vinorsoft.microservices.core.notarization.util.snowflake.Snowflake;

public class NotarizationInitQuery {
    private static String FormatStringToQuery(String query) {
        return query.replaceAll("[\r\n]+", " ");
    }

    public static String DropAllTable() {
        return """
                DROP TABLE "TEST_TABLE";
                DROP PROCEDURE "TEST_TABLE_ADD";
                DROP PROCEDURE "TEST_TABLE_COUNTBY_CSTRING";
                DROP PROCEDURE "TEST_TABLE_DELETE";
                DROP PROCEDURE "TEST_TABLE_GETALL";
                DROP PROCEDURE "TEST_TABLE_GETBYID";
                DROP PROCEDURE "TEST_TABLE_GETMULTIBY_CLONGCSTRING";
                DROP PROCEDURE "TEST_TABLE_GETMULTIBY_PAGING";
                DROP PROCEDURE "TEST_TABLE_GETSINGLEBY_CLONGCSTRING";
                DROP PROCEDURE "TEST_TABLE_UPDATE";
                        """;
    }

    public static String CreateTable() {
        return FormatStringToQuery("""
                CREATE TABLE TEST_TABLE
                (ID NUMBER(19,0),
                 "cLONG" NUMBER(19,0),
                 "cSTRING" VARCHAR2(20),
                 "cCHARACTER" CHAR(1),
                 "cINTEGER" NUMBER(10,0),
                 "cSHORT" NUMBER(5,0),
                 "cDATE" DATE,
                 "cBYTE" NUMBER(3,0),
                 "cFLOAT" NUMBER(19,4),
                 "cDOUBLE" NUMBER(19,4),
                 "cBOOLEAN" NUMBER(1,0),
                 "cBIGINTEGER" NUMBER(38,0),
                 "cBIGDECIMAL" NUMBER(38,0),
                 "cNUMBER" NUMBER(38,0),
                 "cDATE2" DATE,
                 "cTIME" DATE,
                 "cTIMESTAMP" DATE,
                 "cBYTES" LONG RAW,
                 PRIMARY KEY (ID)
                )
                    """);
    }

    public static String CreateAddStoreProcedure() {
        return FormatStringToQuery("""
                CREATE OR REPLACE NONEDITIONABLE PROCEDURE "TEST_TABLE_ADD"
                (
                  pID IN NUMBER
                , pLong IN NUMBER
                , pString IN VARCHAR2
                , pCharacter IN CHAR
                , pInteger IN NUMBER
                , pShort IN NUMBER
                , pDate IN DATE
                , pByte IN NUMBER
                , pFloat IN NUMBER
                , pDouble IN NUMBER
                , pBoolean IN NUMBER
                , pBigInteger IN NUMBER
                , pBigDecimal IN NUMBER
                , pNumber IN NUMBER
                , pDate2 IN DATE
                , pTime IN DATE
                , pTimestamp IN DATE
                , pBytes IN LONG RAW
                ) AS
                BEGIN
                  INSERT INTO TEST_TABLE (
                    ID,
                    "cLONG",
                    "cSTRING",
                    "cCHARACTER",
                    "cINTEGER",
                    "cSHORT",
                    "cDATE",
                    "cBYTE",
                    "cFLOAT",
                    "cDOUBLE",
                    "cBOOLEAN",
                    "cBIGINTEGER",
                    "cBIGDECIMAL",
                    "cNUMBER",
                    "cDATE2",
                    "cTIME",
                    "cTIMESTAMP",
                    "cBYTES")
                   VALUES
                   ( pID
                    , pLong
                    , pString
                    , pCharacter
                    , pInteger
                    , pShort
                    , pDate
                    , pByte
                    , pFloat
                    , pDouble
                    , pBoolean
                    , pBigInteger
                    , pBigDecimal
                    , pNumber
                    , pDate2
                    , pTime
                    , pTimestamp
                    , pBytes);
                END TEST_TABLE_ADD
                    """);
    }

    public static String CreateUpdateStoreProcedure() {
        return """
                CREATE OR REPLACE NONEDITIONABLE PROCEDURE "TEST_TABLE_UPDATE"
                    (
                      pID IN NUMBER
                    , pLong IN NUMBER
                    , pString IN VARCHAR2
                    , pCharacter IN CHAR
                    , pInteger IN NUMBER
                    , pShort IN NUMBER
                    , pDate IN DATE
                    , pByte IN NUMBER
                    , pFloat IN NUMBER
                    , pDouble IN NUMBER
                    , pBoolean IN NUMBER
                    , pBigInteger IN NUMBER
                    , pBigDecimal IN NUMBER
                    , pNumber IN NUMBER
                    , pDate2 IN DATE
                    , pTime IN DATE
                    , pTimestamp IN DATE
                    , pBytes IN LONG RAW
                    ) AS
                    BEGIN
                        UPDATE TEST_TABLE
                        SET
                            "cLONG"=pLong,
                            "cSTRING"=pString,
                            "cCHARACTER"=pCharacter,
                            "cINTEGER"=pInteger,
                            "cSHORT"=pShort,
                            "cDATE"=pDate,
                            "cBYTE"=pByte,
                            "cFLOAT"=pFloat,
                            "cDOUBLE"=pDouble,
                            "cBOOLEAN"=pBoolean,
                            "cBIGINTEGER"=pBigInteger,
                            "cBIGDECIMAL"=pBigDecimal,
                            "cNUMBER"=pNumber,
                            "cDATE2"=pDate2,
                            "cTIME"=pTime,
                            "cTIMESTAMP"=pTimestamp,
                            "cBYTES"=pBytes
                        WHERE ID = pID;
                    END TEST_TABLE_UPDATE;
                        """;
    }

    public static String CreateGetByIDStoreProcedure() {
        return """
                CREATE OR REPLACE NONEDITIONABLE PROCEDURE "TEST_TABLE_GETBYID"
                (
                  pID IN NUMBER
                ) AS
                cur SYS_REFCURSOR;
                BEGIN
                    OPEN cur FOR SELECT * FROM TEST_TABLE WHERE ID = pID;
                    DBMS_SQL.RETURN_RESULT(cur);
                END TEST_TABLE_GETBYID;
                    """;
    }

    public static String CreateGetAllStoreProcedure() {
        return """
                CREATE OR REPLACE NONEDITIONABLE PROCEDURE "TEST_TABLE_GETALL"
                AS
                cur SYS_REFCURSOR;
                BEGIN
                    OPEN cur FOR SELECT * FROM TEST_TABLE;
                    DBMS_SQL.RETURN_RESULT(cur);
                END TEST_TABLE_GETALL;
                    """;
    }

    public static String CreateDeleteProcedure() {
        return """
                CREATE OR REPLACE NONEDITIONABLE PROCEDURE "TEST_TABLE_DELETE" (
                    pID IN NUMBER
                ) AS
                BEGIN
                DELETE FROM TEST_TABLE WHERE ID = pID;
                END TEST_TABLE_DELETE;
                    """;
    }

    public static String CreateGetSingleByProcedure() {
        return """
                CREATE OR REPLACE NONEDITIONABLE PROCEDURE TEST_TABLE_GETSINGLEBY_CLONG_CSTRING (PCLONG IN NUMBER, PCSTRING IN VARCHAR2) AS
                cur SYS_REFCURSOR;
                BEGIN
                  OPEN cur FOR SELECT * FROM TEST_TABLE WHERE "cLONG" > PCLONG AND "cSTRING" LIKE '%'||PCSTRING||'%' AND ROWNUM=1;
                  DBMS_SQL.RETURN_RESULT(cur);
                END TEST_TABLE_GETSINGLEBY_CLONG_CSTRING;
                    """;
    }

    public static String CreateGetMultiByProcedure() {
        return """
                CREATE OR REPLACE NONEDITIONABLE PROCEDURE TEST_TABLE_GETMULTIBY_CLONG_CSTRING (PCLONG IN NUMBER, PCSTRING IN VARCHAR2) AS
                cur SYS_REFCURSOR;
                BEGIN
                  OPEN cur FOR SELECT * FROM TEST_TABLE WHERE "cLONG" > PCLONG AND "cSTRING" LIKE '%'||PCSTRING||'%';
                  DBMS_SQL.RETURN_RESULT(cur);
                END TEST_TABLE_GETMULTIBY_CLONG_CSTRING;
                    """;
    }

    public static String CreateCountByProcedure() {
        return """
                CREATE OR REPLACE NONEDITIONABLE PROCEDURE TEST_TABLE_COUNTBY_CSTRING
                (
                  PCSTRING IN VARCHAR2,
                  PCOUNT OUT NUMBER
                ) AS
                BEGIN
                  SELECT COUNT(*) INTO PCOUNT FROM TEST_TABLE WHERE "cSTRING" LIKE '%'||PCSTRING||'%';
                END TEST_TABLE_COUNTBY_CSTRING;
                    """;
    }

    public static String CreateGetAllByPaging() {
        return """
                CREATE OR REPLACE PROCEDURE TEST_TABLE_GETMULTIBY_PAGING
                (
                  PKEYWORD IN VARCHAR2
                , PPAGE IN NUMBER
                , PPAGESIZE IN NUMBER
                , PORDERBY IN VARCHAR2
                , PSORT IN VARCHAR2
                ) AS
                cur SYS_REFCURSOR;
                queryStr NVARCHAR2(10000);
                BEGIN
                IF PORDERBY='' OR PORDERBY=NULL THEN
                     queryStr := 'SELECT * FROM TEST_TABLE WHERE "cSTRING" LIKE ''%'||PKEYWORD||'%'' OFFSET (:PPAGE*:PPAGESIZE) ROWS FETCH NEXT :PPAGESIZE ROWS ONLY';
                ELSE
                    IF (PSORT = 'ASC') THEN
                        queryStr := 'SELECT * FROM TEST_TABLE WHERE "cSTRING" LIKE ''%'||PKEYWORD||'%'' ORDER BY "'||PORDERBY||'" ASC OFFSET (:PPAGE*:PPAGESIZE) ROWS FETCH NEXT :PPAGESIZE ROWS ONLY';
                        --dbms_output.put_line(queryStr);
                    ELSE
                        queryStr := 'SELECT * FROM TEST_TABLE WHERE "cSTRING" LIKE ''%'||PKEYWORD||'%'' ORDER BY "'||PORDERBY||'" DESC OFFSET (:PPAGE*:PPAGESIZE) ROWS FETCH NEXT :PPAGESIZE ROWS ONLY';
                        --dbms_output.put_line('NOT equal');
                    END IF;

                END IF;
                    OPEN cur FOR queryStr USING PPAGE, PPAGESIZE, PPAGESIZE;
                    DBMS_SQL.RETURN_RESULT(cur);
                END TEST_TABLE_GETMULTIBY_PAGING;
                        """;
    }

    private static Notarization[] listTestRecord;

    public static Notarization[] getListTestItem() {
        return getListTestItem(5, true);
    }

    /**
     * @param count
     * @param withError = true then generate item will throw errors when it have
     *                  been added
     * @return
     */
    public static Notarization[] getListTestItem(Integer count, Boolean withError) {
        if (listTestRecord == null) {
            List<Notarization> listData = new ArrayList<>();
            Lorem lorem = LoremIpsum.getInstance();
            Random random = new Random();

            Snowflake snowflake = new Snowflake(1);

            for (int i = 0; i < count; i++) {
                Notarization item = new Notarization();
                item.setId(snowflake.nextId());

                item.CODE = i < 3 || !withError ? lorem.getWords(1) : lorem.getWords((i + 1) * 5); // test max length
                                                                                                      // = 20
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
                item.LOCATION_NOTARIZATION = "";
                item.TIME_NOTARIZATION = new Date();
                item.STATUS = random.nextInt();
                listData.add(item);
            }
            listTestRecord = listData.toArray(Notarization[]::new);
        }
        return listTestRecord;
    }
}
