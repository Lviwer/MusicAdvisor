# MusicAdvisor 

Command line application that uses Spotify API to show music to the user.




### Features
 * new  - Shows new music from the Spotify API new endpoint. Includes album name, artist(s), and external URL to visit album Spotify page.
 * featured - Shows featured music from Spotify API featured-playlists endpoint. Outputs playlist name, external URL to playlist.
 * categories - Shows a list of valid Spotify categories, which can be used to specify a type of playlist.
 * playlist <category>  - Shows the user a selection of playlists from the specified category. Returns an error if unknown category.
 * auth - Access to the above features is denied until the user authenticates with Spotify and allows access scope to their account. A link to the Spotify authentication endpoint is presented to the user, which will open in a web browser and redirect to localhost with a "success" or "failure" message directing the user back to the app.
 * "Paginated" output. - Users receive query results in pages which can be scrolled through with next and prev.
        The number of results on a page is 5 by default but can be amended using the command line argument -page <n>




