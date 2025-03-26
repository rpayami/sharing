# Mult-user File Sharing System

A server/database for serving files that can be shared among users. Allow users to access files they have permission for, create and share with other users, remove files with safe access.

## Requirements

* User must be able to access files concurrently
* Users must be prevented from concurrently editing the same files
* Uses must be able to create/edit/share/delete their files

Optional:
- Host a web page for uploading, viewing, editing, and downloading these files
- Scripts for running/testing/deploying code locally

## How to run the application
`mvn spring-boot:run`

## How to run tests
`mvn -Dtest=FileControllerTest test`

## Demo Test Plan

Here you can find the related Postman collection: [Postman API Call Collection](https://github.com/rpayami/sharing/blob/master/src/test/resources/postman-collection.json)

**Note:** There are already two test users with the following credentials (username/password) inserted into the database (users/accounts can still be modified through `/api/v1/accounts` API).
- User1: `"u1"/"p1"`
- User2: `"u2"/"p2"`

The current authenticated user (User1 or User2, e.g. by using the "Authorization" Tab in Postman) can be used to send the following demo requests sequentially as a demo test plan.

### Test Plan Step1: "User1 - View All Files"
#### Request

GET - `{{base_url}}/api/v1/files`

#### Response
**Description**: No file is created yet, and the result is empty.

   **Code**: ```200 - OK```

   **Body**: ```[]```

### Test Plan Step2: "User1 - Create File1"
#### Request

POST - `{{base_url}}/api/v1/files`

**Body**:

```
{
    "name": "file1",
    "content": "file1 content"
}
```

#### Response
**Description**: The file gets created and all the VIEW, EDIT, and DELETE permissions get added for the user who has created the file.

**Code**: ```201 - Created```

**Body**: ```1``` (generated Id)

### Test Plan Step 3: "User2 - View All Files"
#### Request
GET - `{{base_url}}/api/v1/files`

#### Response
**Description**: User2 cannot view "file1" as it does not have related VIEW permission

**Code**: `403 - Forbidden`
**Body**:
```
{
    "timestamp": "...",
    "status": 403,
    "error": "Forbidden",
    "message": "Account 2 does not have VIEW permission for file 1",
    "path": "/api/v1/files/1"
}
```
### Test Plan Step 4: - "Share file1 with User2 - Viewable"

#### Request
GET - `{{base_url}}/api/v1/files/1/share/2`

**Body**
```
{
   "viewable": true,
   "editable": false,
   "deletable": false
}
```

#### Response
**Code**: `200 - OK`

### Test Plan Step 5: "User2 - View File1"

#### Request
GET - `{{base_url}}/api/v1/files/1

#### Response
**Description**: Now User2 can view File1 as it has the related VIEW permission

**Code**: `200 - OK`

**Body**
```
{
    "id": 1,
    "name": "file1",
    "content": "file1 content",
    "creationDate": "...",
    "updateDate": null,
    "lastLockedBy": null,
    "locked": false,
    "createdById": 1,
    "updatedById": null
}
```

### Test Plan Step 6: "User2 - Update File1"

#### Request
PUT - `{{base_url}}/api/v1/files/1`

**Body**
```
{
    "name": "file1",
    "content": "file1 - updated"
}
```

#### Response
**Description**: User2 cannot edit File1 as it does not the related EDIT permission

**Code**: `403 - Forbidden`

**Body**
```
 {
    "timestamp": "...",
    "status": 403,
    "error": "Forbidden",
    "message": "Account 2 does not have EDIT permission for file 1",
    "path": "/api/v1/files/1"
 }
 ```

### Test Plan Step 7: "User1 - Share file1 with User2 - Editable"

#### Request
PUT - `{{base_url}}/api/v1/files/1/share/2`

**Body**
```
{
    "viewable": true,
    "editable": true,
    "deletable": false
}
```
#### Response
**Code**: `200 - OK`

### Test Plan Step 8: "User2 - Update File1"

#### Request
PUT - `{{base_url}}/api/v1/files/1`

**Body**
```
{
    "name": "file1",
    "content": "file1 - updated"
}
```

#### Response
**Description**: Now User2 can edit File1 as it has the related EDIT permission

**Code**: `200 - OK`

### Test Plan Step 9: "User1 - Lock file1"

#### Request
PUT - `{{base_url}}/api/v1/files/1/lock`

#### Response
**Code**: `200 - OK`

### Test Plan Step 10: "User2 - Update File1"

#### Request
PUT - `{{base_url}}/api/v1/files/1`

**Body**
```
{
    "name": "file1",
    "content": "file1 - updated"
}
```

#### Response
**Description**: User2 cannot edit File1 as file1 is locked by User1

**Code**: `403 - Forbidden`

**Body**
```
{
    "timestamp": "...",
    "status": 403,
    "error": "Forbidden",
    "message": "Account 1 has locked the file 1",
    "path": "/api/v1/files/1"
}
```

### Test Plan Step 11: "User1 - Unlock File1"

#### Request
PUT - `{{base_url}}/api/v1/files/1/unlock`

#### Response
**Code**: `200 - OK`

### Test Plan Step 12: "User2 - Update File1"

#### Request
PUT - `{{base_url}}/api/v1/files/1`

**Body**
```
{
    "name": "file1",
    "content": "file1 - updated"
}
```
#### Response
**Description**: Now User2 can edit File1 as it is unlocked by User1

**Code**: `200 - OK`

### Test Plan Step 13: "User1 - Unlock file1"

#### Request
PUT - `{{base_url}}/api/v1/files/1/unlock`

#### Response
**Description** File is already unlocked

**Code**: `403 - Forbidden`

**Body**
```
{
    "timestamp": "...",
    "status": 403,
    "error": "Forbidden",
    "message": "File 1 is already unlocked",
    "path": "/api/v1/files/1/unlock"
}
```

### Test Plan Step 14: "User1 - Delete File1"

#### Request
DELETE - `{{base_url}}/api/v1/files/1`

#### Response
**Description**: File gets deleted as User1 has the DELETE permission, and the deleted record gets returned.

**Code**: `200 - OK`

**Body**
```
{
    "id": 1,
    "name": "file1",
    "content": "file1 - updated",
    "creationDate": "...",
    "updateDate": "...",
    "lastLockedBy": 1,
    "locked": false,
    "createdById": 1,
    "updatedById": 2
}
```