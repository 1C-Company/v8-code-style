# Missing temporary file deletion

Once operations with temporary files or directories are completed, delete them. Do not rely on automatic deletion of files and directories upon the next platform startup. It might lead to no more space in the temporary files directory.

## Noncompliant Code Example

## Compliant Solution

## See

- [1Ci Support - Access to the file system from the configuration code, section 4](https://support.1ci.com/hc/en-us/articles/360011122319-Access-to-the-file-system-from-the-configuration-code)
