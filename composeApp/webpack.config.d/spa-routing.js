// Po powrocie z Przelewy24 przeglądarka trafia na /podsumowanieRezerwacji.
// Bez historyApiFallback webpack zwraca 404 zamiast index.html (SPA).
config.devServer = config.devServer || {};
config.devServer.historyApiFallback = {
    index: "/index.html",
    disableDotRule: true,
};
