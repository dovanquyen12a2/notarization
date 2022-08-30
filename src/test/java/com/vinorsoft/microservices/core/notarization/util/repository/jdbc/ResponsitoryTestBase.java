package com.vinorsoft.microservices.core.notarization.util.repository.jdbc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.vinorsoft.microservices.core.notarization.util.repository.Notarization;
import com.vinorsoft.microservices.core.notarization.util.repository_base.jdbc.IRepository;
import com.vinorsoft.microservices.core.notarization.util.repository_base.jdbc.IUnitOfWork;
import com.vinorsoft.microservices.core.notarization.util.repository_base.jdbc.RepositoryBase;

@TestConfiguration
public abstract class ResponsitoryTestBase {
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
