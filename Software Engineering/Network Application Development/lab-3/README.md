# HW3 Comment System

In the third lab, you need to implement a simple version of the commenting system using client-server interaction

## Client

> ###❗️npm version must be 10.\*.\*
![image](https://img.shields.io/badge/React-20232A?style=for-the-badge&logo=react&logoColor=61DAFB)
![image](https://img.shields.io/badge/TypeScript-007ACC?style=for-the-badge&logo=typescript&logoColor=white)
![image](https://img.shields.io/badge/JavaScript-323330?style=for-the-badge&logo=javascript&logoColor=F7DF1E)



It is necessary to implement the client part of the application using technologies: *React / Typescript / Javascript*

The client application can be divided into three parts shown in the screenshot:

![alt text](./screenshots/screen-0.png)

## 1. The top block

Top block with comments, sorted in order **from oldest comments to newest**


> The comment block must be updated every 1 second.

It is necessary to execute a GET request at the URL *"/allComments"* with a timing of 1 second.

> Don't forget to close the connection when the component is unmounted!

## 2. The middle block

The middle block consists of two fields:

- `author`
- `comment`

> The author field is required and limited to 100 characters.

> The comment field is required and limited to 1000 characters.

If the field is not filled in, then after clicking on the “Submit” button,
the message `The field must be filled in`
should appear at the bottom of the field

If the maximum number of characters in the field `author` is exceeded,
then after clicking on the *“Submit”* button,
the message `The maximum field length is 100 characters`
should appear at the bottom of the field.

If the maximum number of characters in the field `comment` is exceeded,
then after clicking on the *“Submit”* button,
the message `The maximum field length is 1000 characters`
should appear at the bottom of the field.

You can find the exact wording in the file `./src/helper.ts`

### Examples:

![alt text](./screenshots/screen-1.png)
![alt text](./screenshots/screen-2.png)
![alt text](./screenshots/screen-3.png)

> Each input field `must` be given an identifier as follows:

#### Author: `"data-testid": "author-field"`

#### Comment: `"data-testid": "comment-field"`

### Example:

```typescript
<input data-testid="author-field" />
```

If you use `Material UI (MUI)` then an example of setting identifiers is as follows

```typescript
<TextField inputProps={{"data-testid": "author-field"}} />
```

## 3. Bottom block:

The bottom block consists of a submit button

When you click on the "Submit" button, the form is validated

- If the form is filled in incorrectly, you must show the messages described in paragraph 2
- if the form is filled in correctly, you must generate a request to the server and send it using the POST method to the
  URL *"/addComment"*

> The submit button must have an identifier

#### Submit button: `"data-testid": "submit-button"`

```typescript
<Button data-testid="submit-button">Send</Button>
```

## Building

In the project directory, you can run:

### `npm install`

To download dependencies and libraries

### `npm start`

To run the app
It'll be open [http://localhost:3000](http://localhost:3000) to view it in the browser (*port 3000 will be used by
default*)

The page will reload if you make edits.\
You will also see any lint errors in the console.

## Test

There are several tests in the `App.test.tsx` file.
To run them, enter the command:

### `npm test`

> Be sure to test your code with these tests before submitting

## Server

> ###️❗gradle version must be 6.\*.\*
![image](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![image](https://img.shields.io/badge/Kotlin-B125EA?style=for-the-badge&logo=kotlin&logoColor=white)

It is necessary to implement the server part of the application using technologies: Spring / Kotlin / Java

You need to implement `CommentController` with the following requests:

> - */allComments* --  **GET** request, must return list of comments (type `CommentModel[]`)

> - */addComment* -- **POST** request, if the new comment (type `CommentModel`)
    > is valid must generate and add identification to the comment and save it,
    > otherwise return error

The CommentModel mustn't be changed

```kotlin
export interface CommentModel {
    id?: string;
    author: string;
    comment: string;
}
```

### Comment requires:

```
- author isn't empty
- comment isn't empty
- author length isn't more than 100 symbols
- comment length isn't more than 1000 symbols
```

### Request codes:

```
- Comment is valid: Request status 200
- Comment is invalid: Request status 400
```

## Building

build without tests: `gradle build -x test`

build with tests: `gradle build`

## Test

There are several tests in the `src/test/kotlin/com/example/back/AppTest` file
To run them, enter the command:

### `gradle test`

> Be sure to test your code with these tests before submitting


