package cash.atto.wallet.datasource

import com.sun.jna.LastErrorException
import com.sun.jna.Memory
import com.sun.jna.Native
import com.sun.jna.Pointer
import com.sun.jna.Structure
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.win32.StdCallLibrary
import com.sun.jna.win32.W32APIOptions

interface CredAdvapi32 : StdCallLibrary {
    /**
     * Credential attributes
     *
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa374790(v=vs.85).aspx
     *
     * typedef struct _CREDENTIAL_ATTRIBUTE {
     * LPTSTR Keyword;
     * DWORD  Flags;
     * DWORD  ValueSize;
     * LPBYTE Value;
     * } CREDENTIAL_ATTRIBUTE, *PCREDENTIAL_ATTRIBUTE;
     *
     */
    open class CREDENTIAL_ATTRIBUTE : Structure() {
        class ByReference : CREDENTIAL_ATTRIBUTE(), Structure.ByReference

        override fun getFieldOrder(): List<String> {
            return mutableListOf(
                "Keyword",
                "Flags",
                "ValueSize",
                "Value"
            )
        }

        /**
         * Name of the application-specific attribute. Names should be of the form <CompanyName>_<Name>.
         * This member cannot be longer than CRED_MAX_STRING_LENGTH (256) characters.
        </Name></CompanyName> */
        var Keyword: String? = null

        /**
         * Identifies characteristics of the credential attribute. This member is reserved and should be originally
         * initialized as zero and not otherwise altered to permit future enhancement.
         */
        var Flags: Int = 0

        /**
         * Length of Value in bytes. This member cannot be larger than CRED_MAX_VALUE_SIZE (256).
         */
        var ValueSize: Int = 0

        /**
         * Data associated with the attribute. By convention, if Value is a text string, then Value should not
         * include the trailing zero character and should be in UNICODE.
         *
         * Credentials are expected to be portable. The application should take care to ensure that the data in
         * value is portable. It is the responsibility of the application to define the byte-endian and alignment
         * of the data in Value.
         */
        var Value: Pointer? = null
    }

    /**
     * Pointer to {@See CREDENTIAL_ATTRIBUTE} struct
     */
    class PCREDENTIAL_ATTRIBUTE : Structure {
        override fun getFieldOrder(): List<String> {
            return listOf("credential_attribute")
        }

        constructor() : super()

        constructor(data: ByteArray) : super(Memory(data.size.toLong())) {
            pointer.write(0, data, 0, data.size)
            read()
        }

        constructor(memory: Pointer?) : super(memory) {
            read()
        }

        var credential_attribute: Pointer? = null
    }

    /**
     * The CREDENTIAL structure contains an individual credential
     *
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa374788(v=vs.85).aspx
     *
     * typedef struct _CREDENTIAL {
     * DWORD                 Flags;
     * DWORD                 Type;
     * LPTSTR                TargetName;
     * LPTSTR                Comment;
     * FILETIME              LastWritten;
     * DWORD                 CredentialBlobSize;
     * LPBYTE                CredentialBlob;
     * DWORD                 Persist;
     * DWORD                 AttributeCount;
     * PCREDENTIAL_ATTRIBUTE Attributes;
     * LPTSTR                TargetAlias;
     * LPTSTR                UserName;
     * } CREDENTIAL, *PCREDENTIAL;
     */
    class CREDENTIAL : Structure {
        override fun getFieldOrder(): List<String> {
            return mutableListOf(
                "Flags",
                "Type",
                "TargetName",
                "Comment",
                "LastWritten",
                "CredentialBlobSize",
                "CredentialBlob",
                "Persist",
                "AttributeCount",
                "Attributes",
                "TargetAlias",
                "UserName"
            )
        }

        constructor() : super()

        constructor(size: Int) : super(Memory(size.toLong()))

        constructor(memory: Pointer?) : super(memory) {
            read()
        }

        /**
         * A bit member that identifies characteristics of the credential. Undefined bits should be initialized
         * as zero and not otherwise altered to permit future enhancement.
         *
         * See MSDN doc for all possible flags
         */
        @JvmField
        var Flags: Int = 0

        /**
         * The type of the credential. This member cannot be changed after the credential is created.
         *
         * See MSDN doc for all possible types
         */
        @JvmField
        var Type: Int = 0

        /**
         * The name of the credential. The TargetName and Type members uniquely identify the credential.
         * This member cannot be changed after the credential is created. Instead, the credential with the old
         * name should be deleted and the credential with the new name created.
         *
         * See MSDN doc for additional requirement
         */
        @JvmField
        var TargetName: String? = null

        /**
         * A string comment from the user that describes this credential. This member cannot be longer than
         * CRED_MAX_STRING_LENGTH (256) characters.
         */
        @JvmField
        var Comment: String? = null

        /**
         * The time, in Coordinated Universal Time (Greenwich Mean Time), of the last modification of the credential.
         * For write operations, the value of this member is ignored.
         */
        @JvmField
        var LastWritten: WinBase.FILETIME? = null

        /**
         * The size, in bytes, of the CredentialBlob member. This member cannot be larger than
         * CRED_MAX_CREDENTIAL_BLOB_SIZE (512) bytes.
         */
        @JvmField
        var CredentialBlobSize: Int = 0

        /**
         * Secret data for the credential. The CredentialBlob member can be both read and written.
         * If the Type member is CRED_TYPE_DOMAIN_PASSWORD, this member contains the plaintext Unicode password
         * for UserName. The CredentialBlob and CredentialBlobSize members do not include a trailing zero character.
         * Also, for CRED_TYPE_DOMAIN_PASSWORD, this member can only be read by the authentication packages.
         *
         * If the Type member is CRED_TYPE_DOMAIN_CERTIFICATE, this member contains the clear test
         * Unicode PIN for UserName. The CredentialBlob and CredentialBlobSize members do not include a trailing
         * zero character. Also, this member can only be read by the authentication packages.
         *
         * If the Type member is CRED_TYPE_GENERIC, this member is defined by the application.
         * Credentials are expected to be portable. Applications should ensure that the data in CredentialBlob is
         * portable. The application defines the byte-endian and alignment of the data in CredentialBlob.
         */
        @JvmField
        var CredentialBlob: Pointer? = null

        /**
         * Defines the persistence of this credential. This member can be read and written.
         *
         * See MSDN doc for all possible values
         */
        @JvmField
        var Persist: Int = 0

        /**
         * The number of application-defined attributes to be associated with the credential. This member can be
         * read and written. Its value cannot be greater than CRED_MAX_ATTRIBUTES (64).
         */
        @JvmField
        var AttributeCount: Int = 0

        /**
         * Application-defined attributes that are associated with the credential. This member can be read
         * and written.
         */
        //notTODO: Need to make this into array
        @JvmField
        var Attributes: CREDENTIAL_ATTRIBUTE.ByReference? = null

        /**
         * Alias for the TargetName member. This member can be read and written. It cannot be longer than
         * CRED_MAX_STRING_LENGTH (256) characters.
         *
         * If the credential Type is CRED_TYPE_GENERIC, this member can be non-NULL, but the credential manager
         * ignores the member.
         */
        @JvmField
        var TargetAlias: String? = null

        /**
         * The user name of the account used to connect to TargetName.
         * If the credential Type is CRED_TYPE_DOMAIN_PASSWORD, this member can be either a DomainName\UserName
         * or a UPN.
         *
         * If the credential Type is CRED_TYPE_DOMAIN_CERTIFICATE, this member must be a marshaled certificate
         * reference created by calling CredMarshalCredential with a CertCredential.
         *
         * If the credential Type is CRED_TYPE_GENERIC, this member can be non-NULL, but the credential manager
         * ignores the member.
         *
         * This member cannot be longer than CRED_MAX_USERNAME_LENGTH (513) characters.
         */
        @JvmField
        var UserName: String? = null
    }

    /**
     * Pointer to {@see CREDENTIAL} struct
     */
    class PCREDENTIAL : Structure {
        override fun getFieldOrder(): List<String> {
            return listOf("credential")
        }

        constructor() : super()

        constructor(data: ByteArray) : super(Memory(data.size.toLong())) {
            pointer.write(0, data, 0, data.size)
            read()
        }

        constructor(memory: Pointer?) : super(memory) {
            read()
        }

        @JvmField
        var credential: Pointer? = null
    }

    /**
     * The CredRead function reads a credential from the user's credential set.
     *
     * The credential set used is the one associated with the logon session of the current token.
     * The token must not have the user's SID disabled.
     *
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa374804(v=vs.85).aspx
     *
     * @param targetName
     * String that contains the name of the credential to read.
     * @param type
     * Type of the credential to read. Type must be one of the CRED_TYPE_* defined types.
     * @param flags
     * Currently reserved and must be zero.
     * @param pcredential
     * Out - Pointer to a single allocated block buffer to return the credential.
     * Any pointers contained within the buffer are pointers to locations within this single allocated block.
     * The single returned buffer must be freed by calling `CredFree`.
     *
     * @return
     * True if CredRead succeeded, false otherwise
     *
     * @throws LastErrorException
     * GetLastError
     */
    @Throws(LastErrorException::class)
    fun CredRead(targetName: String?, type: Int, flags: Int, pcredential: PCREDENTIAL?): Boolean

    /**
     * The CredWrite function creates a new credential or modifies an existing credential in the user's credential set.
     * The new credential is associated with the logon session of the current token. The token must not have the
     * user's security identifier (SID) disabled.
     *
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa375187(v=vs.85).aspx
     *
     * @param credential
     * A CREDENTIAL structure to be written.
     * @param flags
     * Flags that control the function's operation. The following flag is defined.
     * CRED_PRESERVE_CREDENTIAL_BLOB:
     * The credential BLOB from an existing credential is preserved with the same
     * credential name and credential type. The CredentialBlobSize of the passed
     * in Credential structure must be zero.
     *
     * @return
     * True if CredWrite succeeded, false otherwise
     *
     * @throws LastErrorException
     * GetLastError
     */
    @Throws(LastErrorException::class)
    fun CredWrite(credential: CREDENTIAL?, flags: Int): Boolean

    /**
     * The CredDelete function deletes a credential from the user's credential set. The credential set used is the one
     * associated with the logon session of the current token. The token must not have the user's SID disabled.
     *
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa374787(v=vs.85).aspx
     *
     * @param targetName
     * String that contains the name of the credential to read.
     * @param type
     * Type of the credential to delete. Must be one of the CRED_TYPE_* defined types. For a list of the
     * defined types, see the Type member of the CREDENTIAL structure.
     * If the value of this parameter is CRED_TYPE_DOMAIN_EXTENDED, this function can delete a credential that
     * specifies a user name when there are multiple credentials for the same target. The value of the TargetName
     * parameter must specify the user name as Target|UserName.
     * @param flags
     * Reserved and must be zero.
     *
     * @return
     * True if CredDelete succeeded, false otherwise
     *
     * @throws LastErrorException
     * GetLastError
     */
    @Throws(LastErrorException::class)
    fun CredDelete(targetName: String?, type: Int, flags: Int): Boolean

    /**
     * The CredFree function frees a buffer returned by any of the credentials management functions.
     *
     * https://msdn.microsoft.com/en-us/library/windows/desktop/aa374796(v=vs.85).aspx
     *
     * @param credential
     * Pointer to CREDENTIAL to be freed
     *
     * @throws LastErrorException
     * GetLastError
     */
    @Throws(LastErrorException::class)
    fun CredFree(credential: Pointer?)

    companion object {
        val INSTANCE: CredAdvapi32 = Native.load(
            "Advapi32",
            CredAdvapi32::class.java, W32APIOptions.UNICODE_OPTIONS
        )

        /**
         * CredRead flag
         */
        const val CRED_FLAGS_PROMPT_NOW: Int = 0x0002
        const val CRED_FLAGS_USERNAME_TARGET: Int = 0x0004

        /**
         * Type of Credential
         */
        const val CRED_TYPE_GENERIC: Int = 1
        const val CRED_TYPE_DOMAIN_PASSWORD: Int = 2
        const val CRED_TYPE_DOMAIN_CERTIFICATE: Int = 3
        const val CRED_TYPE_DOMAIN_VISIBLE_PASSWORD: Int = 4
        const val CRED_TYPE_GENERIC_CERTIFICATE: Int = 5
        const val CRED_TYPE_DOMAIN_EXTENDED: Int = 6
        const val CRED_TYPE_MAXIMUM: Int = 7 // Maximum supported cred type
        const val CRED_TYPE_MAXIMUM_EX: Int = CRED_TYPE_MAXIMUM + 1000

        /**
         * CredWrite flag
         */
        const val CRED_PRESERVE_CREDENTIAL_BLOB: Int = 0x1

        /**
         * Values of the Credential Persist field
         */
        const val CRED_PERSIST_NONE: Int = 0
        const val CRED_PERSIST_SESSION: Int = 1
        const val CRED_PERSIST_LOCAL_MACHINE: Int = 2
        const val CRED_PERSIST_ENTERPRISE: Int = 3
    }
}