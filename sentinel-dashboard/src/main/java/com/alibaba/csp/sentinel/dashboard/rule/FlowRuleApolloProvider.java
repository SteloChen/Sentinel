/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.rule;

import com.alibaba.csp.sentinel.dashboard.config.ApolloConfigUtil;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.StringUtil;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenNamespaceDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hantianwei@gmail.com
 * @since 1.5.0
 */
@Component("flowRuleApolloProvider")
public class FlowRuleApolloProvider implements DynamicRuleProvider<List<FlowRuleEntity>> {


    private final ApolloOpenApiClient apolloOpenApiClient;
    private final Converter<String, List<FlowRuleEntity>> converter;

    public FlowRuleApolloProvider(ApolloOpenApiClient apolloOpenApiClient, Converter<String, List<FlowRuleEntity>> converter) {
        this.apolloOpenApiClient = apolloOpenApiClient;
        this.converter = converter;
    }

    @Override
    public List<FlowRuleEntity> getRules(String appName) throws Exception {
        String appId = System.getProperty("apollo.appId");
        String flowDataId = ApolloConfigUtil.getFlowDataId(appName);
        OpenNamespaceDTO openNamespaceDTO = apolloOpenApiClient.getNamespace(appId, System.getProperty("apollo.env"), System.getProperty("apollo.cluster"), System.getProperty("apollo.namespace"));
        String rules = openNamespaceDTO
            .getItems()
            .stream()
            .filter(p -> p.getKey().equals(flowDataId))
            .map(OpenItemDTO::getValue)
            .findFirst()
            .orElse("");
        if (StringUtil.isEmpty(rules)) {
            return new ArrayList<>();
        }
        return converter.convert(rules);
    }

}
