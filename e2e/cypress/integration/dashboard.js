describe("Dashboard Test", () => {
  it("Visits the main dashboard", () => {
    cy.visit("http://localhost");
    cy.contains("Hier auf Prosper III");
  });
});
