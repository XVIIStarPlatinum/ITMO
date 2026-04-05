import {useEffect, useState} from "react";
import {EMPTY_FIELD, MAX_LENGTH_100, MAX_LENGTH_1000} from "../helper";
import {CommentModel} from "../model/CommentModel";
import "./Comments.css";
import axios from "axios";

export const Comments = (): JSX.Element => {
    const [comments, setComments] = useState<CommentModel[]>([]);
    const [author, setAuthor] = useState("");
    const [comment, setComment] = useState("");

    const [authorError, setAuthorError] = useState("");
    const [commentError, setCommentError] = useState("");

    useEffect(() => {
        const fetchComments = () => {
            axios.get("/allComments").then((res) => {
                setComments(res.data);
            });
        }
        fetchComments();
        const interval = setInterval(fetchComments, 1000);
        return () => clearInterval(interval);
    }, []);

    const validate = (): boolean => {
        let valid = true;

        setAuthorError("");
        setCommentError("");

        if (!author) {
            setAuthorError(EMPTY_FIELD);
            valid = false;
        } else if (author.length > 100) {
            setAuthorError(MAX_LENGTH_100);
            valid = false;
        }

        if (!comment) {
            setCommentError(EMPTY_FIELD);
            valid = false;
        } else if (comment.length > 1000) {
            setCommentError(MAX_LENGTH_1000);
            valid = false;
        }
        return valid;
    }

    const submit = async () => {
        if (!validate()) {
            return;
        }
        try {
            await axios.post("/addComment", {author, comment});
            setAuthor("");
            setComment("");
        } catch (e) {
        }
    }
    return (
        <div className="comments-root">
            <div className="comments-list">
                {comments.map((c) => (
                    <div className="comment-item" key={c.id}>
                        <div className="comment-author">{c.author}</div>
                        <div className="comment-text">{c.comment}</div>
                    </div>
                ))}
            </div>
            <div>
                <input
                    data-testid="author-field"
                    className="input-author"
                    placeholder="Author"
                    value={author}
                    onChange={(e) => setAuthor(e.target.value)}/>
                {authorError && <div className="error">{authorError}</div>}

                <input
                    data-testid="comment-field"
                    className="input-comment"
                    placeholder="Comment"
                    value={comment}
                    onChange={(e) => setComment(e.target.value)}/>
                {commentError && <div className="error">{commentError}</div>}
            </div>
            <button data-testid="submit-button" className="btn-send" onClick={submit}>
                SEND
            </button>
        </div>
    )
}
