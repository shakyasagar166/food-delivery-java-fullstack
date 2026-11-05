import { CssBaseline, ThemeProvider } from "@mui/material";
import "./App.css";

import darkTheme from "./theme/DarkTheme";
import Routers from "./Routers/Routers";
import { useDispatch, useSelector } from "react-redux";
import { useEffect } from "react";
import { getUser } from "./State/Authentication/Action";
import { findCart } from "./State/Customers/Cart/cart.action";
import {
  getAllRestaurantsAction,
  getRestaurantByUserId,
} from "./State/Customers/Restaurant/restaurant.action";
import ChatBot from "./customers/components/ChatBot/ChatBot";

function App() {
  const dispatch = useDispatch();
  const { auth } = useSelector((store) => store);
  const jwt = localStorage.getItem("jwt");

  useEffect(() => {
    const token = auth.jwt || jwt || localStorage.getItem("jwt");
    if (token) {
      dispatch(getUser(token));
      dispatch(findCart(token));
      dispatch(getAllRestaurantsAction(token));
    }
  }, [auth.jwt]);

  useEffect(() => {
    const token = auth.jwt || jwt || localStorage.getItem("jwt");
    if (auth.user?.role === "ROLE_RESTAURANT_OWNER" && token) {
      dispatch(getRestaurantByUserId(token));
    }
  }, [auth.user]);

  return (
    <ThemeProvider theme={darkTheme}>
      <CssBaseline />
      <Routers />
      <ChatBot />
    </ThemeProvider>
  );
}

export default App;
