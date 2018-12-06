/*
 * Copyright (C) 2007-2018 Crafter Software Corporation. All rights reserved.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.studio.impl.v2.service.security;

import org.craftercms.studio.model.AuthenticationType;

/**
 * Simple class that represents a Studio authentication.
 *
 * @author avasquez
 */
public class Authentication {

    private String username;
    private String token;
    private AuthenticationType authenticationType;

    public Authentication(String username, String token, AuthenticationType authenticationType) {
        this.username = username;
        this.token = token;
        this.authenticationType = authenticationType;
    }

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    public AuthenticationType getAuthenticationType() {
        return authenticationType;
    }

}