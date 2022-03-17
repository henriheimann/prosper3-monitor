describe("My First Test", () => {
  it("Visits the homepage", () => {
    cy.visit("http://localhost");
    cy.contains("Hier auf Prosper III");
  });
});
