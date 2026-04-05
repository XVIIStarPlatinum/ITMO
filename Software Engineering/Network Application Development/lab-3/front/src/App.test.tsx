/**
 * Before submitting your merge request, test it using these tests
 */
import React from 'react';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import axios from "axios";
import App from './App';

describe("appTests", () => {
    const getSpy = jest.spyOn(axios, 'get');
    const postSpy = jest.spyOn(axios, 'post');
    beforeEach(() => {
        getSpy.mockClear();
        postSpy.mockClear();
        getSpy.mockResolvedValue({data: [{id: "dsds", author: "qq", comment: "dfsd"}]});
    })

    test('{CHECK EXISTING FIELDS}: author', async () => {
        render(<App/>)
        const authorField = screen.getByTestId('author-field')
        await waitFor(() => {
            expect(authorField).toBeInTheDocument()
        });
    })

    test('{CHECK EXISTING FIELDS}: comment', async () => {
        render(<App/>)
        const commentField = screen.getByTestId('comment-field')
        await waitFor(() => {
            expect(commentField).toBeInTheDocument()
        });
    })

    test('{CHECK EXISTING FIELDS}: submit', async () => {
        render(<App/>)
        const submitButton = screen.getByTestId('submit-button')
        await waitFor(() => {
            expect(submitButton).toBeInTheDocument()
        });
    })

    test('{CHECK REQUIRED FILED}: author and comment are empty', async () => {
        render(<App/>)
        const submitButton = screen.getByTestId('submit-button')

        fireEvent(
            submitButton,
            new MouseEvent('click', {
                bubbles: true,
                cancelable: true,
            }),
        )

        await waitFor(() => {
            expect(screen.queryAllByText("The field must be filled in")).toHaveLength(2)
        });
    })
});