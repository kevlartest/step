function loadComments() {
    fetch('/list-comments').then(response => response.json()).then((comments) => {
        const commentListElement = document.getElementById('comments-list');
        comments.forEach((comment) => {
            commentListElement.appendChild(createCommentElement(comment));
        })
    });
}

function createCommentElement(comment) {
    const commentElement = document.createElement('li');
    commentElement.className = 'comment';

    const timestamp = new Date();
    timestamp.setUTCMilliseconds(comment.timestamp.seconds);
    
    // Long format the timestamp (e.g. Sunday 9 August 2020, 04:06)
    const options = { hour: 'numeric', minute: 'numeric', weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
    const formattedTimestamp = timestamp.toLocaleDateString('en-IE', options);

    const commentHeading = document.createElement('p');

    const emailElement = document.createElement('span');
    emailElement.className = 'commentEmail';
    emailElement.innerText = comment.email;

    const timestampElement = document.createElement('span');
    timestampElement.className = 'commentTimestamp';
    timestampElement.innerText = formattedTimestamp;

    commentHeading.appendChild(emailElement);
    commentHeading.appendChild(timestampElement);

    const bodyElement = document.createElement('p');
    bodyElement.className = 'commentBody';
    bodyElement.innerText = comment.body;

    commentElement.appendChild(commentHeading);
    commentElement.appendChild(bodyElement);

    return commentElement;
}