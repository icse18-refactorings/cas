/*
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.cas.adaptors.ldap.lppe;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.joda.time.DateTime;
import org.ldaptive.LdapAttribute;
import org.ldaptive.LdapEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The password policy configuration defined by the underlying data source.
 * @author Misagh Moayyed
 * @version 4.0.0
 */
public final class PasswordPolicyConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordPolicyConfiguration.class);

    /**
     * This enumeration defines a selective limited set of ldap user account control flags
     * that indicate various statuses of the user account. The account status
     * is a bitwise flag that may contain one of more of the following values.
     */
    private enum ActiveDirectoryUserAccountControlFlags {
        UAC_FLAG_ACCOUNT_DISABLED(2), UAC_FLAG_LOCKOUT(16), UAC_FLAG_PASSWD_NOTREQD(32), UAC_FLAG_DONT_EXPIRE_PASSWD(65536), 
        UAC_FLAG_PASSWORD_EXPIRED(8388608);

        private int value;

        ActiveDirectoryUserAccountControlFlags(final int id) {
            this.value = id;
        }

        public final int getValue() {
            return this.value;
        }
    }

    /** The ldap converter used in calculating the expiration date attribute value.*/
    @NotNull
    private LdapDateConverter ldapDateConverter = null;

    /** The value that will cause password warning to be bypassed  */
    private List<String> ignorePasswordExpirationWarningFlags = new ArrayList<String>();

    /** Disregard the warning period and warn all users of password expiration */
    private boolean alwaysDisplayPasswordExpirationWarning = false;

    private String passwordExpirationDate;
    private String ignorePasswordExpirationWarning;
    private String passwordExpirationDateAttributeName;

    private int validPasswordNumberOfDays;
    private int passwordWarningNumberOfDays;

    private boolean accountDisabled;
    private boolean accountLocked;
    private boolean accountPasswordMustChange;

    private long userAccountControl = -1;

    /** The custom attribute that indicates the account is disabled **/
    private String accountDisabledAttributeName = null;

    /** The custom attribute that indicates the account is locked **/
    private String accountLockedAttributeName = null;

    /** The custom attribute that indicates the account password must change **/
    private String accountPasswordMustChangeAttributeName = null;

    /** The attribute that indicates the user account status **/
    private String userAccountControlAttributeName = "userAccountControl";

    /** The attribute that contains the data that will determine if password warning is skipped  */
    private String ignorePasswordExpirationWarningAttributeName = null;

    /** Default number of days which the password may be considered valid **/
    private int defaultValidPasswordNumberOfDays = 90;

    /** Default number of days to use when calculating the warning period **/
    private int defaultPasswordWarningNumberOfDays = 30;

    /** Url to the password policy application **/
    private String passwordPolicyUrl;

    /** The attribute that contains the user's warning days */
    private String passwordWarningNumberOfDaysAttributeName = null;

    /** The attribute that contains the number of days the user's password is valid */
    private String validPasswordNumberOfDaysAttributeName = null;

    private String dn;

    public final boolean isAlwaysDisplayPasswordExpirationWarning() {
        return this.alwaysDisplayPasswordExpirationWarning;
    }

    public final void setAlwaysDisplayPasswordExpirationWarning(boolean alwaysDisplayPasswordExpirationWarning) {
        this.alwaysDisplayPasswordExpirationWarning = alwaysDisplayPasswordExpirationWarning;
    }

    public PasswordPolicyConfiguration() {
    }

    public final String getPasswordPolicyUrl() {
        return this.passwordPolicyUrl;
    }

    public void setPasswordPolicyUrl(final String passwordPolicyUrl) {
        this.passwordPolicyUrl = passwordPolicyUrl;
    }

    public void setAccountDisabledAttributeName(final String accountDisabledAttributeName) {
        this.accountDisabledAttributeName = accountDisabledAttributeName;
    }

    public String getAccountDisabledAttributeName() {
        return this.accountDisabledAttributeName;
    }
    
    public void setAccountLockedAttributeName(final String accountLockedAttributeName) {
        this.accountLockedAttributeName = accountLockedAttributeName;
    }

    public String getAccountLockedAttributeName() {
        return this.accountLockedAttributeName;
    }
    
    public void setAccountPasswordMustChangeAttributeName(final String accountPasswordMustChange) {
        this.accountPasswordMustChangeAttributeName = accountPasswordMustChange;
    }

    public String getAccountPasswordMustChangeAttributeName() {
        return this.accountPasswordMustChangeAttributeName;
    }

    public long getUserAccountControl() {
        return this.userAccountControl;
    }

    public void setUserAccountControlAttributeName(final String attr) {
        this.userAccountControlAttributeName = attr;
    }

    public String getUserAccountControlAttributeName() {
        return this.userAccountControlAttributeName;
    }
    
    public void setValidPasswordNumberOfDaysAttributeName(final String validDaysAttributeName) {
        this.validPasswordNumberOfDaysAttributeName = validDaysAttributeName;
    }

    public String getValidPasswordNumberOfDaysAttributeName() {
        return this.validPasswordNumberOfDaysAttributeName;
    }
    
    public void setPasswordWarningNumberOfDaysAttributeName(final String warningDaysAttributeName) {
        this.passwordWarningNumberOfDaysAttributeName = warningDaysAttributeName;
    }

    public String getPasswordWarningNumberOfDaysAttributeName() {
        return this.passwordWarningNumberOfDaysAttributeName;
    }
    
    public void setDefaultValidPasswordNumberOfDays(final int days) {
        this.defaultValidPasswordNumberOfDays = days;
    }

    public void setDefaultPasswordWarningNumberOfDays(final int days) {
        this.defaultPasswordWarningNumberOfDays = days;
    }

    private void setUserAccountControl(final String userAccountControl) {
        if (!StringUtils.isBlank(userAccountControl) && NumberUtils.isNumber(userAccountControl)) {
            this.userAccountControl = Long.parseLong(userAccountControl);
        }
    }

    public boolean isAccountDisabled() {
        return this.accountDisabled;
    }

    private void setAccountDisabled(final boolean accountDisabled) {
        this.accountDisabled = accountDisabled;
    }

    public boolean isAccountLocked() {
        return this.accountLocked;
    }

    private void setAccountLocked(final boolean accountLocked) {
        this.accountLocked = accountLocked;
    }

    public boolean isAccountPasswordMustChange() {
        return this.accountPasswordMustChange;
    }

    private void setAccountPasswordMustChange(final boolean accountPasswordMustChange) {
        this.accountPasswordMustChange = accountPasswordMustChange;
    }

    public String getPasswordExpirationDate() {
        return this.passwordExpirationDate;
    }

    public String getIgnorePasswordExpirationWarningAttributeName() {
        return this.ignorePasswordExpirationWarning;
    }

    public int getValidPasswordNumberOfDays() {
        return this.validPasswordNumberOfDays;
    }

    public int getPasswordWarningNumberOfDays() {
        return this.passwordWarningNumberOfDays;
    }

    private void setPasswordExpirationDate(final String date) {
        this.passwordExpirationDate = date;
    }

    public void setIgnorePasswordExpirationWarningAttributeName(final String value) {
        this.ignorePasswordExpirationWarning = value;
    }

    public void setValidPasswordNumberOfDays(final int valid) {
        this.validPasswordNumberOfDays = valid;
    }

    public void setPasswordWarningNumberOfDays(final int days) {
        this.passwordWarningNumberOfDays = days;
    }

    public void setPasswordExpirationDateAttributeName(final String value) {
        this.passwordExpirationDateAttributeName = value;
    }

    public String getPasswordExpirationDateAttributeName() {
        return this.passwordExpirationDateAttributeName;
    }

    public LdapDateConverter getDateConverter() {
        return this.ldapDateConverter;
    }

    public void setDateConverter(final LdapDateConverter converter) {
        this.ldapDateConverter = converter;
    }

    public String getDn() {
        return this.dn;
    }

    private void setDn(final String dn) {
        this.dn = dn;
    }

    public final boolean isAccountPasswordSetToNeverExpire() {
        final String ignoreCheckValue = getIgnorePasswordExpirationWarningAttributeName();
        boolean ignoreChecks = false;

        if (!StringUtils.isBlank(ignoreCheckValue) && this.ignorePasswordExpirationWarningFlags != null) {
            ignoreChecks = this.ignorePasswordExpirationWarningFlags.contains(ignoreCheckValue);
        }

        if (!ignoreChecks) {
            ignoreChecks = isUserAccountControlBitSet(ActiveDirectoryUserAccountControlFlags.UAC_FLAG_DONT_EXPIRE_PASSWD);
        }
        return ignoreChecks;
    }

    public boolean isUserAccountControlSetToDisableAccount() {
        return isUserAccountControlBitSet(ActiveDirectoryUserAccountControlFlags.UAC_FLAG_ACCOUNT_DISABLED);
    }

    public boolean isUserAccountControlSetToLockAccount() {
        return isUserAccountControlBitSet(ActiveDirectoryUserAccountControlFlags.UAC_FLAG_LOCKOUT);
    }

    public boolean isUserAccountControlSetToExpirePassword() {
        return isUserAccountControlBitSet(ActiveDirectoryUserAccountControlFlags.UAC_FLAG_PASSWORD_EXPIRED);
    }

    public boolean isUserAccountControlBitSet(final ActiveDirectoryUserAccountControlFlags bit) {
        if (getUserAccountControl() > 0) {
            return ((getUserAccountControl() & bit.getValue()) == bit.getValue());
        }
        return false;
    }

    public boolean build(final LdapEntry entry) {

        final String expirationDate = getPasswordPolicyAttributeValue(entry, this.passwordExpirationDateAttributeName);
        if (StringUtils.isBlank(expirationDate)) {
            LOGGER.warn("Password expiration policy cannot be established because the password expiration date is blank.");
            return false;
        }

        setDn(entry.getDn());
        setPasswordExpirationDateAttributeName(this.passwordExpirationDateAttributeName);
        setPasswordExpirationDate(expirationDate);
        setPasswordWarningNumberOfDays(this.defaultPasswordWarningNumberOfDays);
        setValidPasswordNumberOfDays(this.defaultValidPasswordNumberOfDays);

        String attributeValue = getPasswordPolicyAttributeValue(entry, this.passwordWarningNumberOfDaysAttributeName);
        if (attributeValue != null) {
            if (NumberUtils.isNumber(attributeValue)) {
                setPasswordWarningNumberOfDays(Integer.parseInt(attributeValue));
            }
        }

        attributeValue = getPasswordPolicyAttributeValue(entry, this.ignorePasswordExpirationWarningAttributeName);
        if (attributeValue != null) {
            setIgnorePasswordExpirationWarningAttributeName(attributeValue);
        }

        attributeValue = getPasswordPolicyAttributeValue(entry, this.validPasswordNumberOfDaysAttributeName);
        if (attributeValue != null) {
            if (NumberUtils.isNumber(attributeValue)) {
                setValidPasswordNumberOfDays(Integer.parseInt(attributeValue));
            }
        }

        attributeValue = getPasswordPolicyAttributeValue(entry, this.accountDisabledAttributeName);
        if (attributeValue != null) {
            setAccountDisabled(Boolean.valueOf(attributeValue));
        }

        attributeValue = getPasswordPolicyAttributeValue(entry, this.accountLockedAttributeName);
        if (attributeValue != null) {
            setAccountLocked(Boolean.valueOf(attributeValue));
        }

        attributeValue = getPasswordPolicyAttributeValue(entry, this.accountPasswordMustChangeAttributeName);
        if (attributeValue != null) {
            setAccountPasswordMustChange(Boolean.valueOf(attributeValue));
        }

        attributeValue = getPasswordPolicyAttributeValue(entry, this.userAccountControlAttributeName);
        if (attributeValue != null) {
            setUserAccountControl(attributeValue);
        }

        return true;
    }

    private String getPasswordPolicyAttributeValue(final LdapEntry entry, final String attrName) {
        if (attrName != null) {
            final LdapAttribute attribute = entry.getAttribute(attrName);

            if (attribute != null) {
                LOGGER.debug("Retrieved attribute [{}] with value [{}]", attrName, attribute.getStringValue());
                return attribute.getStringValue();
            }
        }
        return null;
    }

    public DateTime convertPasswordExpirationDate() {
        return getDateConverter().convert(this.getPasswordExpirationDate());
    }
}
