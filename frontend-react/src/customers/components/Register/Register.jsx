import React from "react";
import { Formik, Form, Field, ErrorMessage } from "formik";
import * as Yup from "yup";
import {
  Button,
  TextField,
  Typography,
  CssBaseline,
  Container,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { registerUser } from "../../../State/Authentication/Action";

const initialValues = {
  fullName: "",
  email: "",
  password: "",
  role: "ROLE_CUSTOMER",
};

const validationSchema = Yup.object({
  fullName: Yup.string().required("Full Name is required"),
  email: Yup.string()
    .email("Invalid email format")
    .required("Email is required"),
  password: Yup.string()
    .min(6, "Password must be at least 6 characters")
    .required("Password is required"),
  role: Yup.string().required("Role is required"),
});

const RegistrationForm = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();

  const handleSubmit = (values) => {
    console.log("Form values:", values);
    dispatch(registerUser({ userData: values, navigate }));
  };

  return (
    <Container component="main" maxWidth="xs">
      <CssBaseline />
      <div>
        <Typography className="text-center" variant="h5">
          Register
        </Typography>
        <Formik
          initialValues={initialValues}
          validationSchema={validationSchema}
          onSubmit={handleSubmit}
        >
          {({ values, handleChange, handleBlur, touched, errors }) => (
            <Form>
              <Field
                as={TextField}
                variant="outlined"
                margin="normal"
                fullWidth
                label="Full Name"
                name="fullName"
                id="fullName"
                autoComplete="fullName"
                error={touched.fullName && Boolean(errors.fullName)}
                helperText={<ErrorMessage name="fullName" />}
              />
              <Field
                as={TextField}
                variant="outlined"
                margin="normal"
                fullWidth
                label="Email Address"
                name="email"
                id="email"
                autoComplete="email"
                error={touched.email && Boolean(errors.email)}
                helperText={<ErrorMessage name="email" />}
              />
              <Field
                as={TextField}
                variant="outlined"
                margin="normal"
                fullWidth
                label="Password"
                name="password"
                type="password"
                id="password"
                error={touched.password && Boolean(errors.password)}
                helperText={<ErrorMessage name="password" />}
              />
              <FormControl fullWidth margin="normal">
                <InputLabel id="role-select-label">Role</InputLabel>
                <Select
                  labelId="role-select-label"
                  id="role"
                  name="role"
                  value={values.role}
                  label="Role"
                  onChange={handleChange}
                  onBlur={handleBlur}
                >
                  <MenuItem value="ROLE_CUSTOMER">Customer</MenuItem>
                  <MenuItem value="ROLE_RESTAURANT_OWNER">Restaurant Owner</MenuItem>
                </Select>
              </FormControl>
              <Button
                type="submit"
                fullWidth
                variant="contained"
                color="primary"
                sx={{ mt: 3 }}
              >
                Register
              </Button>
            </Form>
          )}
        </Formik>
        <Typography variant="body2" align="center" sx={{ mt: 3 }}>
          Already have an account ?{" "}
          <Button onClick={() => navigate("/account/login")}>Login</Button>
        </Typography>
      </div>
    </Container>
  );
};

export default RegistrationForm;
