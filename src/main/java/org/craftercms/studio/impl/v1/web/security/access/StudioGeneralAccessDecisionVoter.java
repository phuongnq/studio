/*
 * Copyright (C) 2007-2020 Crafter Software Corporation. All Rights Reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 3 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.craftercms.studio.impl.v1.web.security.access;

import org.craftercms.commons.lang.RegexUtils;
import org.craftercms.studio.api.v1.log.Logger;
import org.craftercms.studio.api.v1.log.LoggerFactory;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.FilterInvocation;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import static org.craftercms.studio.api.v2.utils.StudioConfiguration.SECURITY_PUBLIC_URLS;

public class StudioGeneralAccessDecisionVoter extends StudioAbstractAccessDecisionVoter {

    private final static Logger logger = LoggerFactory.getLogger(StudioGeneralAccessDecisionVoter.class);

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection collection) {
        int toRet = authentication.isAuthenticated() ? ACCESS_ABSTAIN : ACCESS_DENIED;
        String requestUri="";
        if (object instanceof FilterInvocation) {
            FilterInvocation filterInvocation = (FilterInvocation) object;
            HttpServletRequest request = filterInvocation.getRequest();
            requestUri = request.getRequestURI().replace(request.getContextPath(), "");
            if (RegexUtils.matchesAny(requestUri, getPublicUrls())) {
                toRet = ACCESS_GRANTED;
            }
        }
        logger.debug("Request: " + requestUri + " - Access: " + toRet);
        return toRet;
    }

    @Override
    public boolean supports(Class clazz) {
        return true;
    }

    protected List<String> getPublicUrls() {
        StringTokenizer st = new StringTokenizer(studioConfiguration.getProperty(SECURITY_PUBLIC_URLS), ",");
        List<String> publicUrls = new ArrayList<String>(st.countTokens());
        while (st.hasMoreTokens()) {
            publicUrls.add(st.nextToken().trim());
        }
        return publicUrls;
    }

}
