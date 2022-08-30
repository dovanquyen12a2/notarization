package com.vinorsoft.microservices.core.notarization.util.repository.r2dbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.vinorsoft.microservices.core.notarization.util.repository.Notarization;
import com.vinorsoft.microservices.core.notarization.util.repository_base.r2dbc.IRepository;
import com.vinorsoft.microservices.core.notarization.util.repository_base.r2dbc.IUnitOfWork;
import com.vinorsoft.microservices.core.notarization.util.repository_base.r2dbc.RepositoryBase;

@TestConfiguration
public class R2DBCResponsitoryTestBase {

    @Autowired
    protected IUnitOfWork unitOfWork;

    @Autowired
    protected IRepository<Notarization> respository;

    static class ResponsitoryTestBaseContextConfiguration {
        @Bean
        public RepositoryBase<Notarization> respositoryImp() {
            return new RepositoryBase<Notarization>(Notarization.class);
        }
    }
}
